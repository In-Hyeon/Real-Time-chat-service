package com.example.springbootpractice.domain.user.repository;

import com.example.springbootpractice.domain.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
