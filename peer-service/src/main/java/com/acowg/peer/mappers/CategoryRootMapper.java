package com.acowg.peer.mappers;

import com.acowg.peer.entities.CategoryEntity;
import com.acowg.peer.entities.DirectoryEntity;
import com.acowg.shared.models.enums.CategoryType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CategoryRootMapper {

    @Mapping(target = "categoryRootPath", source = "categoryRoot")
    @Mapping(target = "category", source = "category", qualifiedByName = "mapCategory")
    DirectoryEntity fromEvent(String categoryRoot, CategoryType category);

    @Named("mapCategory")
    default CategoryEntity mapCategory(CategoryType categoryType) {
        CategoryEntity category = new CategoryEntity();
        category.setCategoryType(categoryType);
        return category;
    }
}
