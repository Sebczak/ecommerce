package com.kodilla.ecommercee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "USERS")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @Column(name = "TOKEN")
    private Integer token;

    @Column(name = "USER_LOCKED")
    private boolean locked = false;

    @Column(name = "TOKEN_CREATION_TIME")
    private LocalDateTime tokenCreationTime;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(targetEntity = Order.class,
            mappedBy = "user",
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(targetEntity = Cart.class,
            orphanRemoval = true,
            mappedBy = "user",
            fetch = FetchType.EAGER)
    private List<Cart> carts = new ArrayList<>();

    public User(String username, String password, String address) {
        this.username = username;
        this.password = password;
        this.address = address;
    }
}

