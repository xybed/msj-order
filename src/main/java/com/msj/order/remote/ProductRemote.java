package com.msj.order.remote;

import com.msj.common.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "msj-product", fallback = ProductRemoteHystrix.class)
public interface ProductRemote {

    @RequestMapping(value = "/product/shopping/cart", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<ProductDto> getProductShoppingCart(@RequestParam("ids") String ids);

    @RequestMapping(value = "/product/stock", method = RequestMethod.GET)
    Integer getProductStock(@RequestParam("id") Integer id);
}
