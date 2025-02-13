package com.acowg.shared.models.dto.collections;

import com.acowg.shared.models.dto.MediaDTO;
import lombok.Data;

import java.util.List;

@Data
public abstract class MediaCollectionDTO {
    private String id;
    private String name;
    private String description;
    private List<MediaDTO> mediaItems; // Reference to media items
}
