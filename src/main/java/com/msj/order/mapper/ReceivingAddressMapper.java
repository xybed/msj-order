package com.msj.order.mapper;


import com.msj.common.entity.ReceivingAddress;

public interface ReceivingAddressMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ReceivingAddress record);

    int insertSelective(ReceivingAddress record);

    ReceivingAddress selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ReceivingAddress record);

    int updateByPrimaryKey(ReceivingAddress record);
}