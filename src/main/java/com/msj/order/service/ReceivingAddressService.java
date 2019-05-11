package com.msj.order.service;


import com.msj.common.dto.ReceivingAddressDto;
import com.msj.common.entity.ReceivingAddress;

import java.util.List;

public interface ReceivingAddressService {
    List<ReceivingAddressDto> getReceivingAddress(Integer userId);

    void addReceivingAddress(ReceivingAddress receivingAddress);

    void updateReceivingAddress(ReceivingAddress receivingAddress);

    void deleteReceivingAddress(Integer id);

    ReceivingAddressDto getDefaultReceivingAddress(Integer userId);
}
