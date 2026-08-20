package com.example.springbootpractice.domain.gift.repository;

import com.example.springbootpractice.domain.gift.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
}
