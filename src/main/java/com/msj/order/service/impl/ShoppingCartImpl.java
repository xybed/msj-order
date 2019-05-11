package com.msj.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.msj.common.constant.Constants;
import com.msj.common.constant.ResultEnum;
import com.msj.common.constant.ServiceException;
import com.msj.common.dto.ProductDto;
import com.msj.common.dto.ShoppingCartDto;
import com.msj.common.entity.ShoppingCart;
import com.msj.common.enums.ProductStatus;
import com.msj.common.enums.ShoppingCartStatus;
import com.msj.order.mapper.ShoppingCartCustomMapper;
import com.msj.order.remote.ProductRemote;
import com.msj.order.service.ShoppingCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ShoppingCartImpl implements ShoppingCartService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ShoppingCartCustomMapper shoppingCartCustomMapper;
    @Resource
    private ProductRemote productRemote;

    /**
     * 1.根据用户id和购物车状态查询购物车列表
     * 2.如果不为空，去商品服务下查询对应商品信息
     * 3.如果商品信息列表不为空（断路或服务异常会为空），赋值给对应购物车
     * @param userId 用户id
     * @return 购物车列表
     */
    @Override
    public List<ShoppingCartDto> getShoppingCarts(Integer userId) {
        List<ShoppingCartDto> shoppingCartList = shoppingCartCustomMapper.queryShoppingCart(userId, ShoppingCartStatus.EFFECTIVE.getCode());
        List<Integer> idList = new ArrayList<>();
        shoppingCartList.forEach(shoppingCart -> {
            idList.add(shoppingCart.getProductId());
        });
        if(idList.size() > 0){
            List<ProductDto> productList = productRemote.getProductShoppingCart(JSON.toJSONString(idList));
            if(productList != null){
                productList.forEach(product -> {
                    for(ShoppingCartDto shoppingCart : shoppingCartList){
                        if(product.getId().intValue() == shoppingCart.getProductId()){
                            shoppingCart.setName(product.getName());
                            shoppingCart.setImage(product.getImage());
                            shoppingCart.setPrice(product.getPrice());
                            shoppingCart.setOriginalPrice(product.getOriginalPrice());
                            shoppingCart.setDiscountPrice(product.getDiscountPrice());
                            shoppingCart.setStatus(product.getStatus());
                            shoppingCart.setStock(product.getStock());
                            break;
                        }
                    }
                });
            }
        }
        return shoppingCartList;
    }

    /**
     * 添加购物车
     * 1.查看购物车中是否存在相同商品，存在则增加数量，不存在到2
     * 2.查看购物车总量，达到50条，则不插入新数据
     * 3.查询此条商品库存是否充足
     * 4.若充足，则插入一条数据
     * @param shoppingCart 商品数据
     */
    @Override
    public void addShoppingCart(ShoppingCart shoppingCart) {
        ShoppingCartDto temp = shoppingCartCustomMapper.queryShoppingCartByProductId(shoppingCart.getUserId(), shoppingCart.getProductId(), ShoppingCartStatus.EFFECTIVE.getCode());
        if(temp != null && temp.getId() != null){
            shoppingCart.setId(temp.getId());
            shoppingCart.setNum(shoppingCart.getNum() + temp.getNum());
            shoppingCart.setGmtModified(new Date());
            shoppingCartCustomMapper.updateByPrimaryKeySelective(shoppingCart);
            return;
        }
        int count = shoppingCartCustomMapper.queryShoppingCartCount(shoppingCart.getUserId(), ShoppingCartStatus.EFFECTIVE.getCode());
        if(count >= Constants.SHOPPING_CART_COUNT){
            throw new ServiceException(ResultEnum.SHOPPING_CART_COUNT_ERROR);
        }
        Integer stock = productRemote.getProductStock(shoppingCart.getProductId());
        if(stock < shoppingCart.getNum())
            throw new ServiceException(ResultEnum.PRODUCT_STOCK_NOT_ENOUGH);
        shoppingCart.setGmtCreate(new Date());
        int result = shoppingCartCustomMapper.insertSelective(shoppingCart);
        if(result != 1)
            throw new ServiceException(ResultEnum.OPERATE_ERROR);
    }

    @Transactional
    @Override
    public void updateShoppingCart(List<ShoppingCart> shoppingCartList) {
        shoppingCartList.forEach(shoppingCart -> {
            shoppingCart.setGmtModified(new Date());
        });
        shoppingCartCustomMapper.updateBatch(shoppingCartList);
    }

    @Override
    public void deleteShoppingCart(List<Integer> idList) {
        shoppingCartCustomMapper.updateShoppingCartStatus(idList, ShoppingCartStatus.INEFFECTIVE.getCode(), new Date());
    }

    /**
     * 清除购物车中无效的商品
     * 1.查询出用户下所有的购物车
     * 2.根据商品id，到商品服务中请求到所有商品的状态
     * 3.根据返回失效商品列表，改变购物车中的状态
     * @param userId 用户id
     */
    @Override
    public void clearInvalidShoppingCart(Integer userId) {
        //1、2步骤可以直接调用查询购物车的接口
        List<ShoppingCartDto> shoppingCartList = getShoppingCarts(userId);
        //3步骤，可以调用删除购物车的接口
        List<Integer> idList = new ArrayList<>();
        shoppingCartList.forEach(shoppingCart -> {
            if(shoppingCart.getStatus().intValue() == ProductStatus.LOWER.getCode()){
                idList.add(shoppingCart.getId());
            }
        });
        deleteShoppingCart(idList);
    }
}
