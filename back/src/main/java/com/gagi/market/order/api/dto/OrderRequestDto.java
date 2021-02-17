package com.gagi.market.order.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderRequestDto {
    private long itemId;

    @Builder
    public OrderRequestDto(long itemId) {
        this.itemId = itemId;
    }
}
