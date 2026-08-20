package com.example.springbootpractice.domain.gift.repository;

import com.example.springbootpractice.domain.gift.entity.GiftMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftMetadataRepository extends JpaRepository<GiftMetadata, Long> {
}
