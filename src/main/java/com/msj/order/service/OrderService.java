package com.msj.order.service;


import com.msj.common.dto.OrderDto;
import com.msj.common.dto.ShoppingCartDto;

import java.util.List;

public interface OrderService {
    int placeOrder(List<ShoppingCartDto> idList, Integer receivingAddressId);

    List<OrderDto> getOrderList(Integer userId, Integer type, Integer pageIndex, Integer pageSize);

    OrderDto getOrderDetail(Integer id);

    void updateOrderStatus(Integer id, Integer status);
}
