package com.acowg.peer.entities;

import com.acowg.shared.models.enums.CategoryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
@Data
@SoftDelete
public class CategoryEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type")
    private CategoryType categoryType;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DirectoryEntity> categoryRoots = new HashSet<>();

    private LocalDateTime deletedAt;

    @PreRemove
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }
}