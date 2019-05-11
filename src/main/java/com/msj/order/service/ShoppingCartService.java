package com.msj.order.service;


import com.msj.common.dto.ShoppingCartDto;
import com.msj.common.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    List<ShoppingCartDto> getShoppingCarts(Integer userId);

    void addShoppingCart(ShoppingCart shoppingCart);

    void updateShoppingCart(List<ShoppingCart> shoppingCartList);

    void deleteShoppingCart(List<Integer> idList);

    void clearInvalidShoppingCart(Integer userId);
}
