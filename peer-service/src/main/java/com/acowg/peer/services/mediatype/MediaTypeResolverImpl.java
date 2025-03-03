package com.acowg.peer.services.mediatype;

import com.acowg.peer.config.MediaTypeConfig;
import com.acowg.peer.entities.MediaEntity;
import com.acowg.peer.utils.PathUtils;
import com.acowg.shared.models.enums.MediaType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MediaTypeResolverImpl implements IMediaTypeResolver {

    private final MediaTypeConfig mediaTypeConfig;

    /**
     * Resolves the media type based on file extension.
     *
     * @param mediaEntity The media entity to resolve media type for
     * @return Resolved MediaType or null if cannot be determined
     */
    @Override
    public MediaType resolveMediaType(@NonNull MediaEntity mediaEntity) {
        // Determine from file extension
        String extension = PathUtils.extractExtension(mediaEntity.getRelativeFilePath());
        return extension != null
                ? mediaTypeConfig.getExtensionToMediaType().get(extension)
                : null;
    }

    /**
     * Checks if a media entity is a video file.
     *
     * @param mediaEntity The media entity to check
     * @return true if the entity is a video, false otherwise
     */
    @Override
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
    @Override
    public boolean isAudio(@NonNull MediaEntity mediaEntity) {
        MediaType type = resolveMediaType(mediaEntity);
        return type == MediaType.AUDIO;
    }
}