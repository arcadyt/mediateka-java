package com.acowg.shared.models.dto;

import lombok.Data;

@Data
public abstract class AbstractMetadataDTO {
    private long duration; // In seconds
    private long bitrate; // In kbps
    private long fileSize; // In bytes
    private String codec; // Codec used
}
