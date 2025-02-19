package com.acowg.peer.entities;

import com.acowg.shared.models.enums.CategoryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category_catalog")
@Getter
@Setter
@NoArgsConstructor
public class CategoryEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type")
    private CategoryType categoryType;

    @OneToMany(mappedBy = "categoryCatalog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CategoryRootEntity> categoryRoots = new HashSet<>();
}