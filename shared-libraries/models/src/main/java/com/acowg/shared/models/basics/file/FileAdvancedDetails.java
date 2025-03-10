package com.acowg.shared.models.basics.file;

import com.acowg.shared.models.basics.HasFileId;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

/**
 * Contains advanced details of a file, including size, duration, and metadata.
 */
@Getter
@Builder(builderClassName = "Bob")
public class FileAdvancedDetails implements HasFileId {

    /**
     * The unique file identifier.
     */
    @EqualsAndHashCode.Include
    private final String fileId;

    /**
     * The size of the file in bytes. Must be zero or positive.
     */
    @PositiveOrZero
    private long sizeInBytes;

    /**
     * The duration of the file in seconds. Must be zero or positive.
     */
    @PositiveOrZero
    private int durationInSeconds;

    /**
     * Metadata about the file, such as codecs, resolution, frame rate, bitrate, and other technical details.
     */
    private Map<String, Objects> metadata;

    /**
     * Creates a new builder initialized with the file ID from the specified HasFileId instance.
     *
     * @param hasFileId The instance providing the file ID.
     * @return A builder preconfigured with the file ID.
     */
    public static Bob builderFrom(HasFileId hasFileId) {
        return new Bob().fileId(hasFileId.getFileId());
    }
}
