package com.example.springbootpractice.domain.gift.entity;

import com.example.springbootpractice.domain.chat.entity.Message;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GIFT_METADATA")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GiftMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false, unique = true)
    private Message message;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false, unique = true)
    private Voucher voucher;

    public static GiftMetadata create(Message message, Voucher voucher) {
        GiftMetadata metadata = new GiftMetadata();
        metadata.message = message;
        metadata.voucher = voucher;
        return metadata;
    }
}
