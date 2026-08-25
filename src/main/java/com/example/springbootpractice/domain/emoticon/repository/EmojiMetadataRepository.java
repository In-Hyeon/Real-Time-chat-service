package com.example.springbootpractice.domain.emoticon.repository;

import com.example.springbootpractice.domain.emoticon.entity.EmojiMetadata;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmojiMetadataRepository extends JpaRepository<EmojiMetadata, Long> {

    List<EmojiMetadata> findAllByMessageIdIn(List<Long> messageIds);
}
