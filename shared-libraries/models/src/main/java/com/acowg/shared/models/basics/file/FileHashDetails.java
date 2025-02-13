package com.acowg.shared.models.basics.file;

import com.acowg.shared.models.basics.HasFileId;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents the hash details of a file, including its identifier and SHA-512 hash.
 */
@Getter
@Builder(builderClassName = "Bob")
public class FileHashDetails implements HasFileId {

    /**
     * The unique identifier for the file.
     */
    @EqualsAndHashCode.Include
    private final String fileId;

    /**
     * The SHA-512 hash of the file. Must not be null or blank.
     */
    @NotBlank
    private final String sha512;

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
