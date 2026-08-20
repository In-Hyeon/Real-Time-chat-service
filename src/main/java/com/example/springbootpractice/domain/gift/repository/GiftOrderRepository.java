package com.example.springbootpractice.domain.gift.repository;

import com.example.springbootpractice.domain.gift.entity.GiftOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftOrderRepository extends JpaRepository<GiftOrder, Long> {
}
