package com.acowg.peer.config;

import com.acowg.peer.utils.PathUtils;
import com.acowg.shared.models.enums.CategoryType;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Slf4j
@Component
@ConfigurationProperties(prefix = "peer.scraping")
public class PeerScrapingConfig {

    private Map<String, List<String>> categories; // Adjusted to store lists of paths

    private Map<CategoryType, List<File>> scrapingCategories; // Parsed and validated map

    private Map<String, List<CategoryToRootPath>> driveToCategories;

    @PostConstruct
    public void initialize() {
        if (MapUtils.isEmpty(categories)) {
            throw new IllegalStateException("Scraping categories must not be null or empty.");
        }

        // Convert string keys to CategoryType safely and map each to a list of File objects
        scrapingCategories = categories.entrySet().stream()
                .map(entry -> CategoryType.tryParse(entry.getKey())
                        .map(type -> Map.entry(type, entry.getValue().stream()
                                .map(File::new)
                                .toList()))
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(entry -> log.info("Loaded category: {} -> {}", entry.getKey(), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (scrapingCategories.isEmpty()) {
            throw new IllegalStateException("No valid categories found in the configuration.");
        }

        // Build drive-to-categories mapping
        driveToCategories = scrapingCategories.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(path -> new CategoryToRootPath(entry.getKey(), path)))
                .collect(Collectors.groupingBy(
                        entry -> PathUtils.extractRootIdentifier(entry.rootPath().toPath()),
                        Collectors.toUnmodifiableList()
                ));

        log.info("Drive to categories mapping initialized successfully.");
    }

    /**
     * Helper record to associate a category with its root path.
     */
    public record CategoryToRootPath(CategoryType category, File rootPath) {
    }
}
