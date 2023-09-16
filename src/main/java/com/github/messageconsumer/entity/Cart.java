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
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "products_id", nullable = false)
    private Product products;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private User users;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "is_ordered", columnDefinition = "tinyint default 0")
    private Boolean isOrdered;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "cart_state", columnDefinition = "int default 0")
    private Integer cartState;

    @Column(name = "failed_causes")
    private String failed_causes;

    @Column(name = "options")
    private String options;

}