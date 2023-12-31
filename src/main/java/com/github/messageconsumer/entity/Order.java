package com.github.messageconsumer.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private User users;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sellers_id", nullable = false)
    private Seller sellers;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "products_id", nullable = false)
    private Product products;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carts_id", nullable = true)
    private Cart carts;

    @Column(name = "order_state")
    private Integer orderState;

    @Column(name = "order_tag")
    private String orderTag;

    @Column(name="quantity")
    private Integer quantity;

    @Column(name= "total_price")
    private Long totalPrice;

    @Column(name="created_at")
    LocalDateTime createdAt;

    @Column(name = "options")
    private String options;

    @Column(name = "failed_causes")
    private String failed_causes;
}