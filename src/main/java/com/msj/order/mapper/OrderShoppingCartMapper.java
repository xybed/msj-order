package com.msj.order.mapper;


import com.msj.common.entity.OrderShoppingCart;

public interface OrderShoppingCartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderShoppingCart record);

    int insertSelective(OrderShoppingCart record);

    OrderShoppingCart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderShoppingCart record);

    int updateByPrimaryKey(OrderShoppingCart record);
}