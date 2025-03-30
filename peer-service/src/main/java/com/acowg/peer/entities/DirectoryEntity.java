package com.acowg.peer.entities;

import com.acowg.shared.models.enums.CategoryType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "directories")
@Data
public class DirectoryEntity {
    @Id
    @UuidGenerator
    private String id;

    @Column(name = "directory_path", unique = true, nullable = false)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_category")
    private CategoryType defaultCategory;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "directory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MediaEntity> mediaFiles = new HashSet<>();
}