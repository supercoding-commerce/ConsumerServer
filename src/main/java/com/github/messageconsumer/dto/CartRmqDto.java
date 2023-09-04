package com.github.messageconsumer.dto;

import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRmqDto {
    private Long cartId;
    private Long userId;
    private Long productId;
    private String productName;
    private Long price;
    private String imageUrl;
    private Integer quantity;
    private Boolean isOrdered;
    private Integer totalPrice;
    private String options;


}
