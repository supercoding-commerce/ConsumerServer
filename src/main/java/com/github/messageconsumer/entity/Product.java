package com.github.messageconsumer.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_id", nullable = false)
    private User users;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "content")
    private String content;

    @Size(max = 255)
    @NotNull
    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl;

    @NotNull
    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "left_amount")
    private Long leftAmount;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @NotNull
    @Column(name = "is_deleted", nullable = false, columnDefinition = "tinyint default 0")
    private Boolean isDeleted;

    @Lob
    @Column(name = "product_category")
    private String productCategory;

    @Lob
    @Column(name = "gender_category")
    private String genderCategory;

    @Lob
    @Column(name = "age_category")
    private String ageCategory;

}