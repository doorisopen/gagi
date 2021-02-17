package com.gagi.market.order.api.dto;

import com.gagi.market.order.domain.Order;
import com.gagi.market.order.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponseDto {
    private long orderId;
    private long itemId;
    private String memberEmail;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;

    public OrderResponseDto(Order order) {
        this.orderId = order.getOrderId();
        this.itemId = order.getItem().getItemId();
        this.memberEmail = order.getMember().getMemberEmail();
        this.orderStatus = order.getOrderStatus();
        this.orderDate = order.getOrderDate();
    }

    public static OrderResponseDto of(Order order) {
        return new OrderResponseDto(order);
    }
}
