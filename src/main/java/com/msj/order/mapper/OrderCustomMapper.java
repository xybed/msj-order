package com.msj.order.mapper;

import com.msj.common.dto.OrderDto;
import com.msj.common.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderCustomMapper {
    int insertSelective(Order record);

    List<OrderDto> queryOrderByStatus(@Param("userId") Integer userId, @Param("status") Integer status);

    OrderDto queryOrderDetail(Integer id);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

}