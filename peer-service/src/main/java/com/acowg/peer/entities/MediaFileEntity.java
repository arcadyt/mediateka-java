package com.acowg.peer.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "media_file", indexes = {
        @Index(name = "idx_catalog_id", columnList = "catalog_id")
})
@Getter
@Setter
@NoArgsConstructor
@SoftDelete
public class MediaFileEntity {
    @Id
    @UuidGenerator
    @Column(name = "luid", updatable = false, nullable = false)
    private String id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "catalog_uuid")
    private String catalogId;

    @Column(name = "relative_file_path")
    private String relativeFilePath;

    @Column(name = "file_size_in_bytes")
    private int fileSizeInBytes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_root_id")
    private CategoryRootEntity categoryRoot;
}