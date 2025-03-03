package com.acowg.peer.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "media_file")
@Data
@SoftDelete
@SQLRestriction("deleted_at IS NULL")
public class MediaEntity implements HasMediaOfferingFields {
    @Id
    @UuidGenerator
    private String id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "catalog_id")
    private String catalogId;

    @Column(name = "relative_file_path")
    private String relativeFilePath;

    @Column(name = "size_in_bytes")
    private int sizeInBytes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "directory_id")
    private DirectoryEntity directory;

    private LocalDateTime deletedAt;

    @PreRemove
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }
}