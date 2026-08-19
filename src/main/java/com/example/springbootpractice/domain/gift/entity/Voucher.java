package com.example.springbootpractice.domain.gift.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "VOUCHER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private GiftOrder order;

    @Column(name = "voucher_code", nullable = false, length = 100)
    private String voucherCode;

    @Column(name = "voucher_status", nullable = false, length = 20)
    private String voucherStatus = "UNUSED";

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    public static Voucher create(GiftOrder order, String voucherCode, LocalDateTime validUntil) {
        Voucher voucher = new Voucher();
        voucher.order = order;
        voucher.voucherCode = voucherCode;
        voucher.validUntil = validUntil;
        return voucher;
    }
}
