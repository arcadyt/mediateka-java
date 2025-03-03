package com.acowg.peer.config;

import com.acowg.peer.entities.CategoryEntity;
import com.acowg.peer.repositories.ICategoryRepository;
import com.acowg.peer.utils.PathUtils;
import com.acowg.shared.models.enums.CategoryType;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Slf4j
@Component
public class PeerScrapingConfig {

    private final ICategoryRepository categoryRepository;

    // Map of drive identifiers to a list of category-to-root-path mappings
    private Map<String, List<CategoryToRootPath>> driveToCategories;

    public PeerScrapingConfig(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    public void initialize() {
        // Retrieve and filter active categories from the database
        List<CategoryEntity> activeCategories = categoryRepository.findAll();

        if (activeCategories.isEmpty()) {
            log.warn("No active media categories found in the database!");
        }

        // Build drive-to-categories mapping
        driveToCategories = activeCategories.stream()
                .flatMap(category -> category.getCategoryRoots().stream()
                        .map(dir -> new CategoryToRootPath(category.getCategoryType(), new File(dir.getPath())))
                )
                .collect(Collectors.groupingBy(
                        entry -> PathUtils.extractRootIdentifier(entry.rootPath().toPath()),
                        Collectors.toUnmodifiableList()
                ));

        log.info("Drive to categories mapping initialized with {} drives", driveToCategories.size());
        driveToCategories.forEach((drive, paths) ->
                log.info("Drive {}: {} media roots", drive, paths.size())
        );
    }

    /**
     * Helper record to associate a category with its root path.
     */
    public record CategoryToRootPath(CategoryType category, File rootPath) {
    }
}