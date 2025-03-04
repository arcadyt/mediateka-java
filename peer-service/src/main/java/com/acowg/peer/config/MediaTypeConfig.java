package com.acowg.peer.config;

import com.acowg.shared.models.enums.MediaType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Getter
@Component
@ConfigurationProperties(prefix = "media.types")
public class MediaTypeConfig {
    private List<String> video;
    private List<String> audio;
    private Map<String, MediaType> extensionToMediaType;

    public void setVideo(List<String> video) {
        if (this.video instanceof ImmutableList) {
            throw new IllegalStateException("Cannot modify video extensions after initialization");
        }
        this.video = video;
    }

    public void setAudio(List<String> audio) {
        if (this.audio instanceof ImmutableList) {
            throw new IllegalStateException("Cannot modify audio extensions after initialization");
        }
        this.audio = audio;
    }

    @PostConstruct
    public void initializeExtensionToMediaTypeMap() {
        this.video = ImmutableList.copyOf(this.video != null ? this.video : ImmutableList.of());
        this.audio = ImmutableList.copyOf(this.audio != null ? this.audio : ImmutableList.of());

        if (this.video.isEmpty() && this.audio.isEmpty()) {
            throw new IllegalStateException("No media type mappings provided in configuration.");
        }

        ImmutableMap.Builder<String, MediaType> builder = ImmutableMap.builder();

        this.video.forEach(ext -> {
            validateExtension(ext, MediaType.VIDEO);
            builder.put(ext.toLowerCase(), MediaType.VIDEO);
        });

        this.audio.forEach(ext -> {
            validateExtension(ext, MediaType.AUDIO);
            builder.put(ext.toLowerCase(), MediaType.AUDIO);
        });

        this.extensionToMediaType = builder.build();
    }

    private void validateExtension(String extension, MediaType mediaType) {
        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("Invalid extension provided for media type: " + mediaType);
        }
    }
}