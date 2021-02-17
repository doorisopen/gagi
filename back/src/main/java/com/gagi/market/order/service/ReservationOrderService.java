package com.gagi.market.order.service;

import com.gagi.market.item.domain.Item;
import com.gagi.market.item.domain.ItemRepository;
import com.gagi.market.member.domain.Member;
import com.gagi.market.member.domain.MemberRepository;
import com.gagi.market.order.domain.Order;
import com.gagi.market.order.domain.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class ReservationOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    public ReservationOrderService(OrderRepository orderRepository, ItemRepository itemRepository, MemberRepository memberRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public Order createOrder(long itemId, String memberEmail) {
        Item findItem = itemRepository.findById(itemId).get();
        Member findMember = memberRepository.findMemberByMemberEmail(memberEmail).get();
        return orderRepository.save(Order.createOrder(findItem, findMember));
    }

    @Override
    public List<Order> findOrdersByMember(String memberEmail) {
        Member findMember = memberRepository.findMemberByMemberEmail(memberEmail).get();
        return orderRepository.findOrdersByMember(findMember);
    }

    @Override
    public Order findOrderByIdAndMember(long orderId, String memberEmail) {
        Member findMember = memberRepository.findMemberByMemberEmail(memberEmail).get();
        return orderRepository.findOrderByOrderIdAndMember(orderId, findMember).get();
    }

    @Override
    public void cancelOrder(long orderId) {
        Order findOrder = orderRepository.findById(orderId).get();
        findOrder.cancelOrder();
    }

    public boolean checkPermissionOfOrder(Long orderId, String memberEmail) {
        Member findMember = memberRepository.findMemberByMemberEmail(memberEmail).orElse(null);
        Order findOrder = orderRepository.findById(orderId).orElse(null);
        return findOrder
                .getMember()
                .getMemberEmail()
                .equals(findMember.getMemberEmail());
    }
}
