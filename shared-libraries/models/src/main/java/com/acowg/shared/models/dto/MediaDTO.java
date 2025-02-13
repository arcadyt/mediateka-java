package com.acowg.shared.models.dto;

import com.acowg.shared.models.enums.CategoryType;
import lombok.Data;

@Data
public abstract class MediaDTO {
    private String id;
    private String title;
    private String description;
    private CategoryType category; // Enum for category
    private AbstractMetadataDTO metadata;
}
