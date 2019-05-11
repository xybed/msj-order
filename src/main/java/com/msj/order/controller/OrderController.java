package com.msj.order.controller;

import com.alibaba.fastjson.JSONArray;
import com.msj.common.constant.Constants;
import com.msj.common.constant.Result;
import com.msj.common.constant.ResultEnum;
import com.msj.common.constant.ResultGenerator;
import com.msj.common.dto.ShoppingCartDto;
import com.msj.common.entity.Order;
import com.msj.common.pojo.OrderCustom;
import com.msj.order.service.OrderService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class OrderController {

    @Resource
    private OrderService orderService;

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public Result placeOrder(@RequestBody OrderCustom custom){
        List<ShoppingCartDto> shoppingCartDtoList = JSONArray.parseArray(custom.getJson(), ShoppingCartDto.class);
        if(StringUtils.isEmpty(shoppingCartDtoList) ||
                shoppingCartDtoList.size() <= 0 ||
                StringUtils.isEmpty(custom.getReceivingAddressId())){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        return ResultGenerator.genSuccessResult(orderService.placeOrder(shoppingCartDtoList, custom.getReceivingAddressId()));
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public Result getOrderList(@RequestParam Integer userId, @RequestParam(required = false) Integer type,
                       @RequestParam(required = false) Integer pageIndex, @RequestParam(required = false) Integer pageSize){
        if(StringUtils.isEmpty(userId)){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        if(StringUtils.isEmpty(pageIndex))
            pageIndex = Constants.PAGE_INDEX;
        if(StringUtils.isEmpty(pageSize))
            pageSize = Constants.PAGE_SIZE;
        return ResultGenerator.genSuccessResult(orderService.getOrderList(userId, type, pageIndex, pageSize));
    }

    @RequestMapping(value = "/orders/{id}", method = RequestMethod.GET)
    public Result getOrderDetail(@PathVariable Integer id){
        if(StringUtils.isEmpty(id)){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        return ResultGenerator.genSuccessResult(orderService.getOrderDetail(id));
    }

    @RequestMapping(value = "/order", method = RequestMethod.PUT)
    public Result updateOrderStatus(@RequestBody Order order){
        if(StringUtils.isEmpty(order) ||
                StringUtils.isEmpty(order.getId()) ||
                StringUtils.isEmpty(order.getStatus())){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        orderService.updateOrderStatus(order.getId(), order.getStatus());
        return ResultGenerator.genSuccessResult("成功");
    }
}
