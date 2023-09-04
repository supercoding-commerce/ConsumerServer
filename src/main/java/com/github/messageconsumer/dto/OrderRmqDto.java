package com.github.messageconsumer.dto;

import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.Order;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.entity.User;
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
    private Integer orderState;
    private Integer quantity;
    private Integer total_price;
    private String options;


}
