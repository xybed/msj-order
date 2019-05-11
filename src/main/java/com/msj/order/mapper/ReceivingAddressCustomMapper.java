package com.msj.order.mapper;

import com.msj.common.dto.ReceivingAddressDto;
import com.msj.common.entity.ReceivingAddress;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ReceivingAddressCustomMapper {
    //查询所有地址列表
    List<ReceivingAddressDto> queryReceivingAddress(@Param("userId") Integer userId, @Param("status") Integer status);

    int queryReceivingAddressCount(@Param("userId") Integer userId, @Param("status") Integer status);

    //把默认地址改为普通
    int updateType2Common(@Param("userId") Integer userId, @Param("typeCommon") Integer typeCommon, @Param("typeDefault") Integer typeDefault);

    //插入一条地址数据
    int insertSelective(ReceivingAddress record);

    //设置地址为无效
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status, @Param("gmtModified") Date gmtModified);

    int updateByPrimaryKeySelective(ReceivingAddress record);

    //查询默认的地址
    ReceivingAddressDto queryByType(@Param("userId") Integer userId, @Param("type") Integer type, @Param("status") Integer status);

}