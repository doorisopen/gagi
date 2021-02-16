package com.gagi.market.order.service;

import com.gagi.market.order.domain.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(long itemId, String memberEmail);
    List<Order> findOrdersByMember(String memberEmail);
    Order findOrderByIdAndMember(long orderId, String memberEmail);
    void cancelOrder(long orderId);
}
