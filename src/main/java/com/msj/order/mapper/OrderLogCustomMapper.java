package com.msj.order.mapper;


import com.msj.common.entity.OrderLog;

public interface OrderLogCustomMapper {

    int insertSelective(OrderLog record);

}