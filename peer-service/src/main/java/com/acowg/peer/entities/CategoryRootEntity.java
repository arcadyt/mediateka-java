package com.acowg.peer.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category_root")
@Getter
@Setter
@NoArgsConstructor
public class CategoryRootEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "category_root_path")
    private String categoryRootPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_catalog_id")
    private CategoryEntity categoryCatalog;

    @OneToMany(mappedBy = "categoryRoot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MediaFileEntity> mediaFiles = new HashSet<>();
}