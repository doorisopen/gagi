package com.gagi.market.order.domain;

import com.gagi.market.item.domain.Item;
import com.gagi.market.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@ToString
@Getter @Setter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;

    //==연관관계 메소드==//
    public void setMember(Member member) {
        this.member = member;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    //==생성 메소드==//
    public static Order createOrder(Member member, Item item) {
        Order order = new Order();
        order.setMember(member);
        order.setItem(item);
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    public void cancelOrder() {
        this.setStatus(OrderStatus.CANCEL);
    }
}
