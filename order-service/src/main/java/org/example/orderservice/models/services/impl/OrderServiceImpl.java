package org.example.orderservice.models.services.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.models.constants.OrderStatus;
import org.example.orderservice.models.dto.requests.CreateOrderDetailRequest;
import org.example.orderservice.models.dto.requests.CreateOrderRequest;
import org.example.orderservice.models.dto.responses.OrderDetailResponse;
import org.example.orderservice.models.dto.responses.OrderResponse;
import org.example.orderservice.models.dto.responses.ProductResponse;
import org.example.orderservice.models.entities.Order;
import org.example.orderservice.models.entities.OrderDetail;
import org.example.orderservice.models.repositories.OrderDetailRepository;
import org.example.orderservice.models.repositories.OrderRepository;
import org.example.orderservice.models.services.OrderService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final OrderDetailRepository orderDetailRepository;
        private final ProductGatewayService productGatewayService;
        private final KafkaTemplate<String, String> kafkaTemplate;

        @Override
        @Transactional
        public OrderResponse createOrder(CreateOrderRequest request) {
                Order order = Order.builder()
                        .customerName(request.customerName())
                        .status(OrderStatus.PENDING)
                        .build();

                Order orderSave = orderRepository.save(order);
                List<OrderDetail> orderDetails = new ArrayList<>();

                // lay san pham set detail
                double total = 0.0;
                for(CreateOrderDetailRequest detailRequest : request.items()){
                        ProductResponse product = productGatewayService.getProductById(detailRequest.productId());
                        OrderDetail orderDetail = OrderDetail.builder()
                                .order(orderSave)
                                .productId(product.id())
                                .quantity(detailRequest.quantity())
                                .unitPrice(product.price())
                                .build();

                        orderDetails.add(orderDetail);
                        orderDetailRepository.save(orderDetail);
                        total += product.price() * detailRequest.quantity();
                }

                orderSave.setTotal(total);
                orderSave.setStatus(OrderStatus.CONFIRM);
                orderRepository.save(order); // cap nhap total

                kafkaTemplate.send("order-created", request.customerEmail());

            return new OrderResponse(
                    orderSave.getId(), request.customerName(), total, OrderStatus.PENDING,
                    orderDetails.stream().map(
                            (o) ->
                                    new OrderDetailResponse(
                                            o.getId(),
                                            o.getProductId(),
                                            productGatewayService.getProductById(o.getProductId()).name(),
                                            o.getQuantity(),
                                            o.getUnitPrice(),
                                            o.getUnitPrice() * o.getQuantity()
                                    )
                    ).toList()
                    );
        }
}
