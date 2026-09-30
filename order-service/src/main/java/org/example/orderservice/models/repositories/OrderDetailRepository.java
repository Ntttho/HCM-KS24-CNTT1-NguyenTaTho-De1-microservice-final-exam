package org.example.orderservice.models.repositories;

import org.example.orderservice.models.dto.responses.OrderDetailResponse;
import org.example.orderservice.models.entities.Order;
import org.example.orderservice.models.entities.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAllByOrder(Order order);
}
