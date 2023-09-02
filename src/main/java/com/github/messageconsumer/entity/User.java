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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 100)
    @Column(name = "password", length = 100)
    private String password;

    @Size(max = 100)
    @NotNull
    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Size(max = 255)
    @Column(name = "telephone")
    private String telephone;

    @Size(max = 255)
    @Column(name = "role")
    private String role;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @Column(name = "created_at")
    private Instant createdAt;

}