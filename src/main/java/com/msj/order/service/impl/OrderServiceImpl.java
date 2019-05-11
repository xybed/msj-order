package com.msj.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.msj.common.constant.Constants;
import com.msj.common.constant.ResultEnum;
import com.msj.common.constant.ServiceException;
import com.msj.common.dto.OrderDto;
import com.msj.common.dto.ProductDto;
import com.msj.common.dto.ShoppingCartDto;
import com.msj.common.entity.Order;
import com.msj.common.entity.OrderLog;
import com.msj.common.entity.OrderShoppingCart;
import com.msj.common.enums.OrderStatus;
import com.msj.common.enums.ProductStatus;
import com.msj.common.enums.ShoppingCartStatus;
import com.msj.order.mapper.OrderCustomMapper;
import com.msj.order.mapper.OrderLogCustomMapper;
import com.msj.order.mapper.OrderShoppingCartCustomMapper;
import com.msj.order.mapper.ShoppingCartCustomMapper;
import com.msj.order.remote.ProductRemote;
import com.msj.order.service.OrderService;
import com.msj.order.util.NumberUtil;
import com.msj.order.util.OrderNumberUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private ShoppingCartCustomMapper shoppingCartCustomMapper;
    @Resource
    private ProductRemote productRemote;
    @Resource
    private OrderCustomMapper orderCustomMapper;
    @Resource
    private OrderShoppingCartCustomMapper orderShoppingCartCustomMapper;
    @Resource
    private OrderLogCustomMapper orderLogCustomMapper;

    /**
     * 下单
     * 1.查询购物车中的商品id列表
     * 2.去商品服务中，查询对应商品的库存
     * 3.如果库存不足，提示库存不足
     * 4.下单，修改shopping_cart表状态，插入一条order记录，批量插入order_shopping_cart记录
     * @param dtoList 购物车列表，包含id和remark
     * @param receivingAddressId 收货地址id
     */
    @Transactional
    @Override
    public int placeOrder(List<ShoppingCartDto> dtoList, Integer receivingAddressId) {
        List<Integer> idList = new ArrayList<>();
        dtoList.forEach(dto -> {
            idList.add(dto.getId());
        });
        List<ShoppingCartDto> shoppingCartDtoList = shoppingCartCustomMapper.queryShoppingCartByIdList(idList);
        List<Integer> productIdList = new ArrayList<>();
        shoppingCartDtoList.forEach(shoppingCartDto -> {
            productIdList.add(shoppingCartDto.getProductId());
        });
        List<ProductDto> productList = productRemote.getProductShoppingCart(JSON.toJSONString(productIdList));
        if(productList != null) {
            productList.forEach(product -> {
                for(ShoppingCartDto shoppingCartDto: shoppingCartDtoList){
                    if(product.getId().intValue() == shoppingCartDto.getProductId()){
                        if(product.getStock() < shoppingCartDto.getNum()){
                            throw new ServiceException(ResultEnum.PRODUCT_STOCK_NOT_ENOUGH);
                        }
                        if(product.getStatus().intValue() == ProductStatus.LOWER.getCode()){
                            throw new ServiceException(ResultEnum.PRODUCT_LOWER_ERROR);
                        }
                        if(shoppingCartDto.getShoppingCartStatus().intValue() != ShoppingCartStatus.EFFECTIVE.getCode()){
                            throw new ServiceException(ResultEnum.SHOPPING_CART_STATUS_ERROR);
                        }
                        shoppingCartDto.setName(product.getName());
                        shoppingCartDto.setImage(product.getImage());
                        shoppingCartDto.setPrice(product.getPrice());
                        shoppingCartDto.setOriginalPrice(product.getOriginalPrice());
                        shoppingCartDto.setDiscountPrice(product.getDiscountPrice());
                        break;
                    }
                }
            });
        }
        //开始下单
        //修改shopping_cart表状态
        shoppingCartCustomMapper.updateShoppingCartStatus(idList, ShoppingCartStatus.ORDER.getCode(), new Date());
        //插入一条order记录
        Order order = new Order();
        order.setOrderNumber(OrderNumberUtil.getOrderNumberByUUId());
        order.setReceivingAddressId(receivingAddressId);
        double totalAmount = 0;
        for(ShoppingCartDto shoppingCartDto : shoppingCartDtoList){
            totalAmount = NumberUtil.add(totalAmount, NumberUtil.multiply(shoppingCartDto.getPrice().doubleValue(), shoppingCartDto.getNum()));
        }
        order.setPayAmount(new BigDecimal(totalAmount).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        order.setStatus(OrderStatus.WAIT_PAY.getCode());
        order.setGmtCreate(new Date());
        orderCustomMapper.insertSelective(order);
        //批量插入order_shopping_cart记录
        List<OrderShoppingCart> orderShoppingCartList = new ArrayList<>();
        shoppingCartDtoList.forEach(shoppingCartDto -> {
            OrderShoppingCart orderShoppingCart = new OrderShoppingCart();
            orderShoppingCart.setOrderId(order.getId());
            orderShoppingCart.setShoppingCartId(shoppingCartDto.getId());
            orderShoppingCart.setName(shoppingCartDto.getName());
            orderShoppingCart.setImage(shoppingCartDto.getImage().substring(Constants.URL_PREFIX.length()));
            orderShoppingCart.setPrice(shoppingCartDto.getPrice());
            orderShoppingCart.setOriginalPrice(shoppingCartDto.getOriginalPrice());
            orderShoppingCart.setDiscountPrice(shoppingCartDto.getDiscountPrice());
            for(ShoppingCartDto dto : dtoList){
                if(dto.getId().intValue() == shoppingCartDto.getId()){
                    if(StringUtils.isEmpty(dto.getRemark())){
                        orderShoppingCart.setRemark("");
                    }else {
                        orderShoppingCart.setRemark(dto.getRemark());
                    }
                }
            }
            orderShoppingCartList.add(orderShoppingCart);
        });
        orderShoppingCartCustomMapper.insertBatch(orderShoppingCartList);
        return order.getId();
    }

    @Override
    public List<OrderDto> getOrderList(Integer userId, Integer type, Integer pageIndex, Integer pageSize) {
        PageHelper.startPage(pageIndex, pageSize);
        List<OrderDto> orderList = orderCustomMapper.queryOrderByStatus(userId, type);
        orderList.forEach(order -> {
            order.getProducts().forEach(product -> {
                product.setImage(Constants.URL_PREFIX + product.getImage());
            });
        });
        return orderList;
    }

    @Override
    public OrderDto getOrderDetail(Integer id) {
        OrderDto order = orderCustomMapper.queryOrderDetail(id);
        order.getProducts().forEach(product -> {
            product.setImage(Constants.URL_PREFIX + product.getImage());
        });
        return order;
    }

    /**
     * 1.查看要改成的订单状态是什么
     * ---付款操作，待付款->待发货，0->1，付款时间更新（以后是支付宝微信的回调）
     * ---取消订单，待付款->交易失败，0->6
     * ---确认收货，待收货->待评价，2->3，成交时间更新
     * ---退款操作，（待发货、待收货）->退款，（1、2）->4（以后是支付宝微信的回调）
     * ---评价操作，待评价->交易成功，3->5
     * ---删除订单，（待评价、退款、交易成功、交易失败）->删除，（3、4、5、6）->7
     * 2.根据id查order_number和status
     * 3.status符合修改条件则修改
     * 3.插入一条订单流水记录
     * @param id 订单id
     * @param status 要改成的订单状态
     */
    @Transactional
    @Override
    public void updateOrderStatus(Integer id, Integer status) {
        Order order = orderCustomMapper.selectByPrimaryKey(id);
        Integer beforeStatus = order.getStatus();
        order.setId(id);
        order.setStatus(status);
        order.setGmtModified(new Date());
        if(status.intValue() == OrderStatus.WAIT_SEND.getCode() && beforeStatus.intValue() == OrderStatus.WAIT_PAY.getCode()){
            order.setPayTime(new Date());
            orderCustomMapper.updateByPrimaryKeySelective(order);
            insertOrderLog(order.getOrderNumber(), beforeStatus, status);
        }else if(status.intValue() == OrderStatus.FAIL.getCode() && beforeStatus.intValue() == OrderStatus.WAIT_PAY.getCode()){
            orderCustomMapper.updateByPrimaryKeySelective(order);
            insertOrderLog(order.getOrderNumber(), beforeStatus, status);
        }else if(status.intValue() == OrderStatus.WAIT_COMMENT.getCode() && beforeStatus.intValue() == OrderStatus.WAIT_DELIVERY.getCode()){
            order.setDealTime(new Date());
            orderCustomMapper.updateByPrimaryKeySelective(order);
            insertOrderLog(order.getOrderNumber(), beforeStatus, status);
        }else if(status.intValue() == OrderStatus.REFUND.getCode() &&
                (beforeStatus.intValue() == OrderStatus.WAIT_SEND.getCode() || beforeStatus.intValue() == OrderStatus.WAIT_DELIVERY.getCode())){
            orderCustomMapper.updateByPrimaryKeySelective(order);
            insertOrderLog(order.getOrderNumber(), beforeStatus, status);
        }else if(status.intValue() == OrderStatus.SUCCESS.getCode() && beforeStatus.intValue() == OrderStatus.SUCCESS.getCode()){
            orderCustomMapper.updateByPrimaryKeySelective(order);
            insertOrderLog(order.getOrderNumber(), beforeStatus, status);
        }else if(status.intValue() == OrderStatus.DELETE.getCode() &&
                (beforeStatus >= OrderStatus.WAIT_COMMENT.getCode() && beforeStatus <= OrderStatus.FAIL.getCode())){
            orderCustomMapper.updateByPrimaryKeySelective(order);
            insertOrderLog(order.getOrderNumber(), beforeStatus, status);
        }
    }

    private void insertOrderLog(String orderNumber, Integer beforeStatus, Integer afterStatus){
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderNumber(orderNumber);
        orderLog.setBeforeStatus(beforeStatus);
        orderLog.setAfterStatus(afterStatus);
        orderLog.setGmtCreate(new Date());
        orderLogCustomMapper.insertSelective(orderLog);
    }
}
