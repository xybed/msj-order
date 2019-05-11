package com.msj.order.mapper;

import com.msj.common.dto.ShoppingCartDto;
import com.msj.common.entity.ShoppingCart;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ShoppingCartCustomMapper {
    List<ShoppingCartDto> queryShoppingCart(@Param("userId") Integer userId, @Param("status") Integer status);

    ShoppingCartDto queryShoppingCartByProductId(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("status") Integer status);

    //单条修改商品数量
    int updateByPrimaryKeySelective(ShoppingCart record);

    int queryShoppingCartCount(@Param("userId") Integer userId, @Param("status") Integer status);

    int insertSelective(ShoppingCart record);

    //批量修改商品数量
    int updateBatch(@Param("shoppingCartList") List<ShoppingCart> shoppingCartList);

    int updateShoppingCartStatus(@Param("idList") List<Integer> idList, @Param("status") Integer status, @Param("gmtModified") Date gmtModified);

    //根据id查购物车列表
    List<ShoppingCartDto> queryShoppingCartByIdList(@Param("idList") List<Integer> idList);
}