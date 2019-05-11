package com.msj.order.controller;

import com.alibaba.fastjson.JSONArray;
import com.msj.common.constant.Result;
import com.msj.common.constant.ResultEnum;
import com.msj.common.constant.ResultGenerator;
import com.msj.common.entity.ShoppingCart;
import com.msj.common.pojo.ShoppingCartCustom;
import com.msj.order.service.ShoppingCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@RestController
public class ShoppingCartController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ShoppingCartService shoppingCartService;

    @RequestMapping(value = "/shopping/carts", method = RequestMethod.GET)
    public Result getShoppingCarts(@RequestParam("user_id") Integer userId){
        if(StringUtils.isEmpty(userId)){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        return ResultGenerator.genSuccessResult(shoppingCartService.getShoppingCarts(userId));
    }

    @RequestMapping(value = "/shopping/cart", method = RequestMethod.POST)
    public Result addShoppingCart(@RequestBody ShoppingCart shoppingCart){
        if(StringUtils.isEmpty(shoppingCart.getUserId()) ||
                StringUtils.isEmpty(shoppingCart.getProductId()) ||
                StringUtils.isEmpty(shoppingCart.getNum())){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        shoppingCartService.addShoppingCart(shoppingCart);
        return ResultGenerator.genSuccessResult("添加成功");
    }

    @RequestMapping(value = "/shopping/cart", method = RequestMethod.PUT)
    public Result updateShoppingCart(@RequestBody ShoppingCartCustom custom){
        List<ShoppingCart> shoppingCartList = JSONArray.parseArray(custom.getJson(), ShoppingCart.class);
        if(StringUtils.isEmpty(shoppingCartList) || shoppingCartList.size() <= 0){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        shoppingCartService.updateShoppingCart(shoppingCartList);
        return ResultGenerator.genSuccessResult("编辑成功");
    }

    @RequestMapping(value = "/shopping/cart", method = RequestMethod.DELETE)
    public Result deleteShoppingCart(String json){
        try {
            json = URLDecoder.decode(json, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            json = "[]";
        }
        List<Integer> idList = JSONArray.parseArray(json, Integer.class);
        if(idList == null || idList.size() <= 0){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        shoppingCartService.deleteShoppingCart(idList);
        return ResultGenerator.genSuccessResult("删除成功");
    }

    @RequestMapping(value = "/shopping/cart/invalid", method = RequestMethod.PUT)
    public Result clearInvalidShoppingCart(@RequestBody ShoppingCart shoppingCart){
        if(StringUtils.isEmpty(shoppingCart.getUserId())){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        shoppingCartService.clearInvalidShoppingCart(shoppingCart.getUserId());
        return ResultGenerator.genSuccessResult("清除成功");
    }
}
