package com.acowg.peer.mappers;

import com.acowg.peer.dto.CategoryTypesResponse;
import com.acowg.shared.models.enums.CategoryType;
import org.mapstruct.Mapper;

import java.util.Arrays;

@Mapper(componentModel = "spring")
public interface CategoryTypeMapper {

    default CategoryTypesResponse toCategoryTypesResponse() {
        return new CategoryTypesResponse(Arrays.asList(CategoryType.values()));
    }
}