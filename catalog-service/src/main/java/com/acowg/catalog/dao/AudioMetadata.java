package com.acowg.catalog.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AudioMetadata extends AbstractMetadata {
    private String artist;
    private String genre;
}
