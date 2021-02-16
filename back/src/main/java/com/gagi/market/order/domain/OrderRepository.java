package com.gagi.market.order.domain;

import com.gagi.market.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOrdersByMember(Member member);
    Optional<Order> findOrderByOrderIdAndMember(long orderId, Member member);
}
