package com.msj.order.service.impl;

import com.msj.common.constant.Constants;
import com.msj.common.constant.ResultEnum;
import com.msj.common.constant.ServiceException;
import com.msj.common.dto.ReceivingAddressDto;
import com.msj.common.entity.ReceivingAddress;
import com.msj.common.enums.ReceivingAddressStatus;
import com.msj.common.enums.ReceivingAddressType;
import com.msj.order.mapper.ReceivingAddressCustomMapper;
import com.msj.order.service.ReceivingAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class ReceivingAddressServiceImpl implements ReceivingAddressService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ReceivingAddressCustomMapper receivingAddressCustomMapper;

    @Override
    public List<ReceivingAddressDto> getReceivingAddress(Integer userId) {
        return receivingAddressCustomMapper.queryReceivingAddress(userId, ReceivingAddressStatus.EFFECTIVE.getCode());
    }

    /**
     * 1.设置地址状态为有效，创建时间为当前时间
     * 2.查询数据中是否有10条数据，满了则不插入，没满到3
     * 3.如果是添加默认地址，把之前的默认地址修改掉
     * 4.插入一条新数据
     * @param receivingAddress 地址数据
     */
    @Transactional
    @Override
    public void addReceivingAddress(ReceivingAddress receivingAddress) {
        int count = receivingAddressCustomMapper.queryReceivingAddressCount(receivingAddress.getUserId(), ReceivingAddressStatus.EFFECTIVE.getCode());
        if(count >= Constants.RECEIVING_ADDRESS_COUNT){
            throw new ServiceException(ResultEnum.ADDRESS_COUNT_ERROR);
        }
        receivingAddress.setStatus(ReceivingAddressStatus.EFFECTIVE.getCode());
        receivingAddress.setGmtCreate(new Date());
        if(ReceivingAddressType.DEFAULT.getCode().intValue() == receivingAddress.getType().intValue()){
            receivingAddressCustomMapper.updateType2Common(receivingAddress.getUserId(),
                    ReceivingAddressType.COMMON.getCode(), ReceivingAddressType.DEFAULT.getCode());
        }
        int result = receivingAddressCustomMapper.insertSelective(receivingAddress);
        if(result != 1)
            throw new ServiceException(ResultEnum.OPERATE_ERROR);
    }

    /**
     * 1.如果是改为默认地址，把之前的默认地址修改掉
     * 2.设置地址状态为无效，设置修改时间
     * 3.更新数据
     * 4.插入一条新数据
     * @param receivingAddress 地址数据
     */
    @Transactional
    @Override
    public void updateReceivingAddress(ReceivingAddress receivingAddress) {
        if(ReceivingAddressType.DEFAULT.getCode().intValue() == receivingAddress.getType().intValue()){
            receivingAddressCustomMapper.updateType2Common(receivingAddress.getUserId(),
                    ReceivingAddressType.COMMON.getCode(), ReceivingAddressType.DEFAULT.getCode());
        }
        int result = receivingAddressCustomMapper.updateStatus(receivingAddress.getId(), ReceivingAddressStatus.INEFFECTIVE.getCode(), new Date());
        if(result != 1)
            throw new ServiceException(ResultEnum.OPERATE_ERROR);
        receivingAddress.setStatus(ReceivingAddressStatus.EFFECTIVE.getCode());
        receivingAddress.setGmtCreate(new Date());
        receivingAddress.setId(null);
        result = receivingAddressCustomMapper.insertSelective(receivingAddress);
        if(result != 1)
            throw new ServiceException(ResultEnum.OPERATE_ERROR);
    }

    @Override
    public void deleteReceivingAddress(Integer id) {
        ReceivingAddress receivingAddress = new ReceivingAddress();
        receivingAddress.setId(id);
        receivingAddress.setStatus(ReceivingAddressStatus.INEFFECTIVE.getCode());
        receivingAddress.setGmtModified(new Date());
        int result = receivingAddressCustomMapper.updateByPrimaryKeySelective(receivingAddress);
        if(result != 1)
            throw new ServiceException(ResultEnum.OPERATE_ERROR);
    }

    @Override
    public ReceivingAddressDto getDefaultReceivingAddress(Integer userId) {
        return receivingAddressCustomMapper.queryByType(userId, ReceivingAddressType.DEFAULT.getCode(), ReceivingAddressStatus.EFFECTIVE.getCode());
    }
}
