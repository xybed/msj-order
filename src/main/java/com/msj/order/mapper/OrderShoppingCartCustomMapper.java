package com.msj.order.mapper;

import com.msj.common.entity.OrderShoppingCart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderShoppingCartCustomMapper {
    int insertBatch(@Param("list") List<OrderShoppingCart> orderShoppingCartList);
}