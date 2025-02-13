package com.acowg.shared.models.basics.file;

import com.acowg.shared.models.basics.HasFileId;
import com.acowg.shared.models.enums.CategoryType;
import com.acowg.shared.models.enums.MediaType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * Represents the basic details of a file, including its identifier, path, category, and media type.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class FileBasicDetails implements HasFileId {

    /**
     * The unique identifier for the file. Defaults to a randomly generated UUID.
     */
    @Builder.Default
    @EqualsAndHashCode.Include
    private final String fileId = UUID.randomUUID().toString();

    /**
     * The relative path of the file as a list of strings.
     * Each element represents a part of the path and must not be null or blank.
     */
    private final List<String> relativePath;

    /**
     * The category of the file. Must not be null.
     */
    @NotNull
    private final CategoryType category;

    /**
     * The media type of the file. Must not be null.
     */
    @NotNull
    private final MediaType media;

    /**
     * Validates that the relativePath list is not empty and does not contain null or blank strings.
     *
     * @return true if all elements in relativePath are non-null and non-blank, false otherwise.
     */
    @AssertTrue(message = "The relativePath list must not contain null or blank strings.")
    private boolean isRelativePathValid() {
        return CollectionUtils.isNotEmpty(relativePath) &&
                relativePath.stream().allMatch(StringUtils::isNotBlank);
    }
}
