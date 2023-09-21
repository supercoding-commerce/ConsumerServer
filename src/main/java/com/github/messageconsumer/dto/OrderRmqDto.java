package com.github.messageconsumer.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRmqDto {
    private Long orderId;
    private Long productId;
    private Long cartId;
    private Long userId;
    private Long sellerId;
    private Integer orderState;
    private String orderTag;
    private Integer quantity;
    private Long total_price;
    private String options;


}
