package com.gagi.market.order.api.dto;

import com.gagi.market.item.domain.Item;
import com.gagi.market.member.domain.Member;
import com.gagi.market.order.domain.Order;
import com.gagi.market.order.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponseDto {
    private long orderId;
    private Item item;
    private Member member;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;

    public OrderResponseDto(Order order) {
        this.orderId = order.getOrderId();
        this.item = order.getItem();
        this.member = order.getMember();
        this.orderStatus = order.getOrderStatus();
        this.orderDate = order.getOrderDate();
    }

    public static OrderResponseDto of(Order order) {
        return new OrderResponseDto(order);
    }
}
