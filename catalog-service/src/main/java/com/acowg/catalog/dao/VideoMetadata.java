package com.acowg.catalog.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VideoMetadata extends AbstractMetadata {
    private String resolution; // e.g., 1920x1080
    private boolean sovietMovie;
    private String studio; // Only for Soviet movies
}
