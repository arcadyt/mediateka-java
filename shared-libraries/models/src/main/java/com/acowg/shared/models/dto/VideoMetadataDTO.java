package com.acowg.shared.models.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VideoMetadataDTO extends AbstractMetadataDTO {
    private String resolution; // e.g., 1920x1080
    private boolean sovietMovie; // Whether the movie is Soviet
    private String studio; // Only applicable if sovietMovie is true
}
