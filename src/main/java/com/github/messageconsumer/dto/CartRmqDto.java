package com.github.messageconsumer.dto;

import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRmqDto {
    private User user;
    private Long productId;
    private String productName;
    private Long price;
    private String imageUrl;
    private Integer quantity;
    private Boolean isOrdered;
    private Integer totalPrice;
    private String options;
    private LocalDateTime createdAt;

    public static CartRmqDto fromEntity(Cart cart){
        return CartRmqDto.builder()
                .user(cart.getUsers())
                .productId(cart.getProducts().getId())
                .isOrdered(cart.getIsOrdered())
                .quantity(cart.getQuantity())
                .options(cart.getOptions())
                .createdAt(cart.getCreatedAt())
                .build();
    }

//    public static Cart toEntity(CartRmqDto cartRmqDto){
//        return Cart.builder()
//                .users(cartRmqDto.getUser())
//                .products(cartRmqDto.getProduct())
//                .isOrdered(cartRmqDto.getIsOrdered())
//                .quantity(cartRmqDto.getQuantity())
//                .options(cartRmqDto.getOptions())
//                .createdAt(cartRmqDto.getCreatedAt())
//                .build();
//    }
}
