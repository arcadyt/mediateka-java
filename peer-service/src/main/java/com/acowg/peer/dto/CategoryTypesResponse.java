package com.acowg.peer.dto;

import com.acowg.shared.models.enums.CategoryType;
import lombok.Value;

import java.util.List;

@Value
public class CategoryTypesResponse {
    List<CategoryType> categoryTypes;
}