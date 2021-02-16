package com.gagi.market.order.domain;

import com.gagi.market.item.domain.Item;
import com.gagi.market.item.domain.ItemRepository;
import com.gagi.market.member.domain.Member;
import com.gagi.market.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ItemRepository itemRepository;

    @BeforeEach
    public void setup() {
        Member member1 = Member.builder()
                .memberEmail("member1@gagi.com")
                .memberPw("test")
                .memberAddress("가지특별시 가지동")
                .memberPhoneNumber("010-1234-5678")
                .build();
        memberRepository.save(member1);
        Member member2 = Member.builder()
                .memberEmail("member2@gagi.com")
                .memberPw("test")
                .memberAddress("가지특별시 가지동")
                .memberPhoneNumber("010-1234-5678")
                .build();
        memberRepository.save(member2);
    }

    @DisplayName("주문을 생성한다")
    @Test
    public void createOrder() throws Exception {
        //given
        Member seller = memberRepository.findMemberByMemberEmail("member1@gagi.com").get();
        Member buyer = memberRepository.findMemberByMemberEmail("member2@gagi.com").get();
        Item item = Item.builder()
                .itemName("가지10kg")
                .itemDescription("맛좋은 가지")
                .itemPrice(10000)
                .itemCategory("야채")
                .itemLocation("가지마을")
                .build();
        item.setMember(seller);
        itemRepository.save(item);

        //when
        Order order = Order.createOrder(item, buyer);
        orderRepository.save(order);

        //then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(order.getItem().getItemName()).isEqualTo(item.getItemName());
        assertThat(order.getMember().getMemberEmail()).isEqualTo(buyer.getMemberEmail());
    }

    @DisplayName("주문을 취소한다")
    @Test
    public void cancelOrder() throws Exception {
        //given
        Member seller = memberRepository.findMemberByMemberEmail("member1@gagi.com").get();
        Member buyer = memberRepository.findMemberByMemberEmail("member2@gagi.com").get();
        Item item = Item.builder()
                .itemName("가지10kg")
                .itemDescription("맛좋은 가지")
                .itemPrice(10000)
                .itemCategory("야채")
                .itemLocation("가지마을")
                .build();
        item.setMember(seller);
        itemRepository.save(item);
        Order order = Order.createOrder(item, buyer);
        orderRepository.save(order);
        //when
        order.cancelOrder();

        //then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(order.getItem().getItemName()).isEqualTo(item.getItemName());
        assertThat(order.getMember().getMemberEmail()).isEqualTo(buyer.getMemberEmail());
    }

    @DisplayName("회원의 주문 내역을 조회한다")
    @Test
    public void findOrdersByMember() throws Exception {
        Member seller = memberRepository.findMemberByMemberEmail("member1@gagi.com").get();
        Member buyer = memberRepository.findMemberByMemberEmail("member2@gagi.com").get();
        for (int i = 0; i < 10; i++) {
            Item item = Item.builder()
                    .itemName("가지10kg")
                    .itemDescription("맛좋은 가지")
                    .itemPrice(i+10000)
                    .itemCategory("야채")
                    .itemLocation("가지마을")
                    .build();
            item.setMember(seller);
            itemRepository.save(item);
            Order order = Order.createOrder(item, buyer);
            orderRepository.save(order);
        }
        //when
        List<Order> findOrders = orderRepository.findOrdersByMember(buyer);

        //then
        assertThat(findOrders.size()).isEqualTo(10);
        assertThat(findOrders.get(0).getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(findOrders.get(0).getMember().getMemberEmail()).isEqualTo(buyer.getMemberEmail());
    }

    @DisplayName("회원의 주문 상세 내역을 조회한다")
    @Test
    public void findOrderByOrderIdAndMember() throws Exception {
        Member seller = memberRepository.findMemberByMemberEmail("member1@gagi.com").get();
        Member buyer = memberRepository.findMemberByMemberEmail("member2@gagi.com").get();
        Item item = Item.builder()
                .itemName("가지10kg")
                .itemDescription("맛좋은 가지")
                .itemPrice(10000)
                .itemCategory("야채")
                .itemLocation("가지마을")
                .build();
        item.setMember(seller);
        itemRepository.save(item);
        Order order = orderRepository.save(Order.createOrder(item, buyer));

        //when
        Order findOrder = orderRepository.findOrderByOrderIdAndMember(order.getOrderId(), buyer).get();

        //then
        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(findOrder.getMember().getMemberEmail()).isEqualTo(buyer.getMemberEmail());
        assertThat(findOrder.getItem().getItemName()).isEqualTo("가지10kg");
        assertThat(findOrder.getItem().getItemPrice()).isEqualTo(10000);
    }
}