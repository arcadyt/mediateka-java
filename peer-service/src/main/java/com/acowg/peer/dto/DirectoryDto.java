package com.acowg.peer.dto;

import com.acowg.shared.models.enums.CategoryType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DirectoryDto {
    private String id;
    private String path;
    private CategoryType defaultCategory;
    private Set<MediaDto> mediaFiles;
}