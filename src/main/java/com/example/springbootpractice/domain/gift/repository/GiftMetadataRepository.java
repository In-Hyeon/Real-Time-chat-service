package com.example.springbootpractice.domain.gift.repository;

import com.example.springbootpractice.domain.gift.entity.GiftMetadata;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftMetadataRepository extends JpaRepository<GiftMetadata, Long> {

    boolean existsByVoucherId(Long voucherId);

    List<GiftMetadata> findAllByMessageIdIn(List<Long> messageIds);
}
