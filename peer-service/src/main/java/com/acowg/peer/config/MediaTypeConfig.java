package com.acowg.peer.config;

import com.acowg.shared.models.enums.MediaType;
import com.google.common.collect.ImmutableMap;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Component
@ConfigurationProperties(prefix = "media.types")
public class MediaTypeConfig {

    private List<String> video = Collections.emptyList();
    private List<String> audio = Collections.emptyList();
    private Map<String, MediaType> extensionToMediaType;

    /**
     * Populates the reverse mapping as an immutable map from file extensions to media types
     * after the properties are loaded.
     */
    @PostConstruct
    public void initializeExtensionToMediaTypeMap() {
        ImmutableMap.Builder<String, MediaType> builder = ImmutableMap.builder();

        if (video.isEmpty() && audio.isEmpty()) {
            throw new IllegalStateException("No media type mappings provided in configuration.");
        }

        video.forEach(ext -> {
            validateExtension(ext, MediaType.VIDEO);
            builder.put(ext.toLowerCase(), MediaType.VIDEO);
        });

        audio.forEach(ext -> {
            validateExtension(ext, MediaType.AUDIO);
            builder.put(ext.toLowerCase(), MediaType.AUDIO);
        });

        extensionToMediaType = builder.build();
    }

    /**
     * Validates the extension format and logs a warning if invalid.
     *
     * @param extension The file extension to validate.
     * @param mediaType The media type associated with the extension.
     */
    private void validateExtension(String extension, MediaType mediaType) {
        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("Invalid extension provided for media type: " + mediaType);
        }
    }
}
