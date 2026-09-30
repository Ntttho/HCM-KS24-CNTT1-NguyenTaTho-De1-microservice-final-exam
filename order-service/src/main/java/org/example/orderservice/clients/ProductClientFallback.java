package org.example.orderservice.clients;

import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.models.constants.OrderStatus;
import org.example.orderservice.models.dto.responses.OrderResponse;

@Slf4j
public class ProductClientFallback {
    public OrderResponse fallbackFindProduct(){
        log.warn("Create order is failed");
        return new OrderResponse(null, null, null, OrderStatus.CANCELED, null);
    }
}
