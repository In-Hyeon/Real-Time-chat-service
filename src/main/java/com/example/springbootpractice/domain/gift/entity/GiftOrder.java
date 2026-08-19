package com.example.springbootpractice.domain.gift.entity;

import com.example.springbootpractice.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GIFT_ORDER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GiftOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private GiftProduct product;

    @Column(name = "order_status", nullable = false, length = 20)
    private String orderStatus = "SUCCESS";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static GiftOrder create(User sender, User receiver, GiftProduct product) {
        GiftOrder order = new GiftOrder();
        order.sender = sender;
        order.receiver = receiver;
        order.product = product;
        order.createdAt = LocalDateTime.now();
        return order;
    }
}
