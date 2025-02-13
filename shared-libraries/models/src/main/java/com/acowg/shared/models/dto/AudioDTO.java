package com.acowg.shared.models.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AudioDTO extends MediaDTO {
    private AudioMetadataDTO metadata; // Audio-specific metadata
}
