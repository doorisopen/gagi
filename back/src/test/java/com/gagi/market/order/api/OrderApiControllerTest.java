package com.gagi.market.order.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gagi.market.item.domain.Item;
import com.gagi.market.item.domain.ItemRepository;
import com.gagi.market.member.api.dto.SessionMember;
import com.gagi.market.member.domain.Member;
import com.gagi.market.member.domain.MemberRepository;
import com.gagi.market.order.api.dto.OrderRequestDto;
import com.gagi.market.order.domain.Order;
import com.gagi.market.order.domain.OrderRepository;
import com.gagi.market.order.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderApiControllerTest {
    private static final String LOCALHOST = "http://localhost:";
    private static final String ORDER_API_URI = OrderApiController.ORDER_API_URI;

    @LocalServerPort
    private int port;
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    protected MockHttpSession session;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        Member member = memberRepository.save(Member.builder()
                .memberEmail("test@gagi.com")
                .memberPw("test")
                .memberAddress("가지특별시 가지동")
                .memberPhoneNumber("010-1234-5678")
                .build());
        Item item = Item.builder()
                .itemName("가지10kg")
                .itemDescription("맛좋은가지")
                .itemCategory("식품")
                .itemPrice(10000)
                .itemLocation("제주도")
                .build();
        Member buyer = memberRepository.save(Member.builder()
                .memberEmail("dasanda@gagi.com")
                .memberPw("test")
                .memberAddress("서울특별시")
                .memberPhoneNumber("010-1234-5678")
                .build());
        item.setMember(member);
        itemRepository.save(item);
        session = new MockHttpSession();
        session.setAttribute("SESSION_MEMBER", new SessionMember(buyer));
    }

    @DisplayName("주문서 등록을 성공한다")
    @Test
    public void createOrderIsSuccess() throws Exception {
        //given
        String url = LOCALHOST + port + ORDER_API_URI;

        Item findItem = itemRepository.findAll().get(0);
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .itemId(findItem.getItemId())
                .build();
        //when
        mockMvc.perform(
                post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        //then
        List<Order> orders = orderRepository.findAll();
        assertThat(orders.size()).isEqualTo(1);
        assertThat(orders.get(0).getOrderStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(orders.get(0).getItem().getItemName()).isEqualTo("가지10kg");
        assertThat(orders.get(0).getMember().getMemberEmail()).isEqualTo("dasanda@gagi.com");
    }

    @DisplayName("회원의 주문내역을 조회한다")
    @Test
    public void findOrdersByMember() throws Exception {
        //given
        String url = LOCALHOST + port + ORDER_API_URI;
        SessionMember sessionMember = (SessionMember) session.getAttribute("SESSION_MEMBER");
        Item item = itemRepository.findAll().get(0);
        Member member = memberRepository.findMemberByMemberEmail(sessionMember.getMemberEmail()).get();
        orderRepository.save(Order.createOrder(item, member));
        //when
        mockMvc.perform(
                get(url)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //then
        List<Order> orders = orderRepository.findOrdersByMember(member);
        assertThat(orders.size()).isEqualTo(1);
    }

    @DisplayName("회원의 주문 상세 정보를 조회한다")
    @Test
    public void findOrderByIdAndMember() throws Exception {
        //given
        String url = LOCALHOST + port + ORDER_API_URI;
        SessionMember sessionMember = (SessionMember) session.getAttribute("SESSION_MEMBER");
        Item item = itemRepository.findAll().get(0);
        Member member = memberRepository.findMemberByMemberEmail(sessionMember.getMemberEmail()).get();
        Order order = orderRepository.save(Order.createOrder(item, member));
        //when
        mockMvc.perform(
                get(url+"/"+order.getOrderId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //then
        Order findOrder = orderRepository.findOrderByOrderIdAndMember(order.getOrderId(), member).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(findOrder.getMember().getMemberEmail()).isEqualTo("dasanda@gagi.com");
    }

    @DisplayName("주문 취소를 성공한다")
    @Test
    public void cancelOrderIsSuccess() throws Exception {
        //given
        String url = LOCALHOST + port + ORDER_API_URI;
        SessionMember sessionMember = (SessionMember) session.getAttribute("SESSION_MEMBER");
        Item item = itemRepository.findAll().get(0);
        Member member = memberRepository.findMemberByMemberEmail(sessionMember.getMemberEmail()).get();
        Order order = orderRepository.save(Order.createOrder(item, member));
        //when
        mockMvc.perform(
                get(url+"/"+order.getOrderId()+"/cancel")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //then
        Order findOrder = orderRepository.findOrderByOrderIdAndMember(order.getOrderId(), member).get();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(findOrder.getMember().getMemberEmail()).isEqualTo("dasanda@gagi.com");
    }
}