package com.acowg.peer.repositories;

import com.acowg.peer.entities.CategoryEntity;
import com.acowg.shared.models.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICategoryRepository extends JpaRepository<CategoryEntity, String> {
    Optional<CategoryEntity> findByCategoryType(CategoryType categoryType);
}
