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

    public static OrderRmqDto fromEntity(Order order){
        Product product = order.getProducts();
        return OrderRmqDto.builder()
                .cartId(order.getCarts().getId())
                .userId(order.getUsers().getId())
                .productId(product.getId())
                .quantity(order.getQuantity())
                .total_price(order.getTotal_price())
                .orderState(order.getOrderState())
                .options(order.getOptions())
                .build();
    }

}
