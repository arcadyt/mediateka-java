package com.acowg.peer.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "media_files")
@Data
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
    private long sizeInBytes;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_id")
    private DirectoryEntity directory;
}