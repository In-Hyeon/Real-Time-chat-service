package com.example.springbootpractice.domain.user.repository;

import com.example.springbootpractice.domain.user.entity.Profile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    List<Profile> findAllByUserId(Long userId);

    long countByUserId(Long userId);
}
