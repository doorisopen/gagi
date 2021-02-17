package com.gagi.market.order.api;

import com.gagi.market.config.auth.LoginMember;
import com.gagi.market.member.api.dto.SessionMember;
import com.gagi.market.order.api.dto.OrderRequestDto;
import com.gagi.market.order.api.dto.OrderResponseDto;
import com.gagi.market.order.domain.Order;
import com.gagi.market.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.gagi.market.order.api.OrderApiController.ORDER_API_URI;
import static com.gagi.market.order.api.dto.OrderResponseDto.of;

@RestController
@RequestMapping(ORDER_API_URI)
public class OrderApiController {
    public static final String ORDER_API_URI = "/api/v1.0/orders";

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@LoginMember SessionMember member,
                                                        @RequestBody OrderRequestDto requestDto) {
        Order order = orderService.createOrder(requestDto.getItemId(), member.getMemberEmail());
        return ResponseEntity
                .created(URI.create(ORDER_API_URI + "/" + order.getOrderId()))
                .body(OrderResponseDto.of(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> findOrdersByMember(@LoginMember SessionMember member) {
        List<OrderResponseDto> findOrders = orderService.findOrdersByMember(member.getMemberEmail())
                .stream()
                .map(OrderResponseDto::of)
                .collect(Collectors.toList());
        return ResponseEntity
                .ok()
                .body(findOrders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> findOrderByIdAndMember(@LoginMember SessionMember member,
                                                                   @PathVariable long orderId) {
        if (!orderService.checkPermissionOfOrder(orderId, member.getMemberEmail())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        OrderResponseDto findOrder = of(orderService.findOrderByIdAndMember(orderId, member.getMemberEmail()));
        return ResponseEntity
                .ok()
                .body(findOrder);
    }

    @GetMapping("/{orderId}/cancel")
    public ResponseEntity<HttpStatus> cancelOrder(@LoginMember SessionMember member,
                                                  @PathVariable long orderId) {
        if (!orderService.checkPermissionOfOrder(orderId, member.getMemberEmail())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
