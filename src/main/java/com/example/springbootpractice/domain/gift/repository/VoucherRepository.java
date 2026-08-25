package com.example.springbootpractice.domain.gift.repository;

import com.example.springbootpractice.domain.gift.entity.Voucher;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    List<Voucher> findAllByOrderIdIn(List<Long> orderIds);
}
