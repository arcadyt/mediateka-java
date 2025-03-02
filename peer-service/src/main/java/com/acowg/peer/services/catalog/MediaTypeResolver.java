package com.acowg.peer.services.catalog;

import com.acowg.peer.config.MediaTypeConfig;
import com.acowg.peer.entities.MediaEntity;
import com.acowg.shared.models.enums.MediaType;
import com.google.common.base.Strings;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaTypeResolver {

    private final MediaTypeConfig mediaTypeConfig;

    /**
     * Resolves the media type based on file extension.
     *
     * @param mediaEntity The media entity to resolve media type for
     * @return Resolved MediaType or null if cannot be determined
     */
    public MediaType resolveMediaType(@NonNull MediaEntity mediaEntity) {
        // Determine from file extension
        String extension = extractExtension(mediaEntity.getRelativeFilePath());
        return extension != null
                ? mediaTypeConfig.getExtensionToMediaType().get(extension)
                : null;
    }

    /**
     * Extracts file extension from relative file path.
     *
     * @param filePath Path of the file
     * @return Lowercase file extension or null
     */
    private String extractExtension(String filePath) {
        if (Strings.isNullOrEmpty(filePath)) {
            return null;
        }

        int lastDotIndex = filePath.lastIndexOf('.');
        return (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1)
                ? filePath.substring(lastDotIndex + 1).toLowerCase()
                : null;
    }

    /**
     * Checks if a media entity is a video file.
     *
     * @param mediaEntity The media entity to check
     * @return true if the entity is a video, false otherwise
     */
    public boolean isVideo(@NonNull MediaEntity mediaEntity) {
        MediaType type = resolveMediaType(mediaEntity);
        return type == MediaType.VIDEO;
    }

    /**
     * Checks if a media entity is an audio file.
     *
     * @param mediaEntity The media entity to check
     * @return true if the entity is audio, false otherwise
     */
    public boolean isAudio(@NonNull MediaEntity mediaEntity) {
        MediaType type = resolveMediaType(mediaEntity);
        return type == MediaType.AUDIO;
    }
}