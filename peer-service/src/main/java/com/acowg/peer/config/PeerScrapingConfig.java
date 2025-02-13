package com.acowg.peer.config;

import com.acowg.peer.utils.PathUtils;
import com.acowg.shared.models.enums.CategoryType;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Component
@ConfigurationProperties(prefix = "peer")
public class PeerScrapingConfig {

    private String name;
    private Map<CategoryType, String> scrapingCategories;

    /**
     * Precomputed mapping of drive letters to categories and their root paths.
     * Initialized after properties are loaded.
     */
    private Map<String, List<CategoryToRootPath>> driveToCategories;

    /**
     * Initializes the drive-to-categories mapping after the properties are loaded.
     */
    @PostConstruct
    public void initializeDriveToCategories() {
        if (scrapingCategories == null || scrapingCategories.isEmpty()) {
            throw new IllegalStateException("Scraping categories must not be null or empty.");
        }

        driveToCategories = scrapingCategories.entrySet().stream()
                .map(entry -> new CategoryToRootPath(entry.getKey(), new File(entry.getValue())))
                .collect(Collectors.groupingBy(
                        entry -> PathUtils.extractRootIdentifier(entry.rootPath().toPath()),
                        Collectors.toList()
                ));
    }
}
