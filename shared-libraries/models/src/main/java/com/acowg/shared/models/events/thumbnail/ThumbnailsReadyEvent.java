package com.acowg.shared.models.events.thumbnail;

import com.acowg.shared.models.basics.HasFileId;
import com.acowg.shared.models.events.AEvent;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents an event indicating that thumbnails have been generated and are ready for a specific file.
 */
@Getter
@Builder(builderClassName = "Bob")
@EqualsAndHashCode
public class ThumbnailsReadyEvent extends AEvent implements HasFileId {
    @NotBlank
    private final String fileId;

    /**
     * The encoded representation of the small thumbnail.
     */
    @NotBlank
    private final String smallThumbnailBase64;

    /**
     * The encoded representation of the large thumbnail.
     */
    @NotBlank
    private final String largeThumbnailBase64;
}
