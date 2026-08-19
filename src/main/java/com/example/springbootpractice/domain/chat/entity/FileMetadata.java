package com.example.springbootpractice.domain.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FILE_METADATA")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "file_url", nullable = false, length = 255)
    private String fileUrl;

    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    public static FileMetadata create(Message message, String fileUrl, String originalName, long fileSize) {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.message = message;
        fileMetadata.fileUrl = fileUrl;
        fileMetadata.originalName = originalName;
        fileMetadata.fileSize = fileSize;
        return fileMetadata;
    }
}
