package com.acowg.shared.models.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AudioMetadataDTO extends AbstractMetadataDTO {
    private String artist;
    private String genre;
}
