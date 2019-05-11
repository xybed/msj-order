package com.msj.order.remote;


import com.msj.common.dto.ProductDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductRemoteHystrix implements ProductRemote{
    @Override
    public List<ProductDto> getProductShoppingCart(String ids) {
        return null;
    }

    @Override
    public Integer getProductStock(Integer id) {
        return 0;
    }
}
