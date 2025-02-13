package com.acowg.shared.models.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VideoDTO extends MediaDTO {
    private VideoMetadataDTO metadata; // Video-specific metadata
}
