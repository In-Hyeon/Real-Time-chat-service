package com.example.springbootpractice.domain.gift.repository;

import com.example.springbootpractice.domain.gift.entity.GiftProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftProductRepository extends JpaRepository<GiftProduct, Long> {
}
