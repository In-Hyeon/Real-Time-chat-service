package com.example.springbootpractice.domain.gift.repository;

import com.example.springbootpractice.domain.gift.entity.GiftOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftOrderRepository extends JpaRepository<GiftOrder, Long> {

    List<GiftOrder> findAllBySenderId(Long senderId);

    List<GiftOrder> findAllByReceiverId(Long receiverId);
}
