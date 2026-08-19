package com.example.springbootpractice.domain.gift.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GIFT_PRODUCT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GiftProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_name", nullable = false, length = 50)
    private String productName;

    @Column(name = "brand_name", nullable = false, length = 50)
    private String brandName;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static GiftProduct create(String productName, String brandName, BigDecimal price) {
        GiftProduct giftProduct = new GiftProduct();
        giftProduct.productName = productName;
        giftProduct.brandName = brandName;
        giftProduct.price = price;
        giftProduct.createdAt = LocalDateTime.now();
        return giftProduct;
    }
}
