package com.acowg.catalog.dao;

import lombok.Data;

@Data
public abstract class AbstractMetadata {
    private long duration; // In seconds
    private long bitrate; // In kbps
    private long fileSize; // In bytes
    private String codec; // Codec used
}
