package com.acowg.peer.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "directory",
        indexes = @Index(name = "idx_directory_path", columnList = "directory_path", unique = true))
@Data
@SoftDelete
@SQLRestriction("deleted_at IS NULL")
public class DirectoryEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "directory_path", unique = true, nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToMany(mappedBy = "directory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MediaEntity> mediaFiles = new HashSet<>();

    private LocalDateTime deletedAt;

    @PreRemove
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }
}
