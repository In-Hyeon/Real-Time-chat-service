package com.example.springbootpractice.domain.chat.repository;

import com.example.springbootpractice.domain.chat.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
}
