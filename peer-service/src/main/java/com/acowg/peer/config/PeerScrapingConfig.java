package com.acowg.peer.config;

import com.acowg.peer.entities.DirectoryEntity;
import com.acowg.peer.repositories.IDirectoryRepository;
import com.acowg.peer.utils.PathUtils;
import com.acowg.shared.models.enums.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PeerScrapingConfig {
    private final IDirectoryRepository directoryRepository;

    public record CategoryToRootPath(CategoryType category, File rootPath) {}

    /**
     * Gets the current mapping of drives to category-path pairs.
     * Always returns fresh data from the database.
     */
    public Map<String, List<CategoryToRootPath>> getDriveToCategories() {
        List<DirectoryEntity> directories = directoryRepository.findAll();

        if (directories.isEmpty()) {
            log.warn("No media directories found in the database!");
        }

        Map<String, List<CategoryToRootPath>> mapping = directories.stream()
                .map(dir -> new CategoryToRootPath(dir.getDefaultCategory(), new File(dir.getPath())))
                .collect(Collectors.groupingBy(
                        entry -> PathUtils.extractRootIdentifier(entry.rootPath().toPath()),
                        Collectors.toUnmodifiableList()
                ));

        log.debug("Drive to categories mapping has {} drives", mapping.size());
        return mapping;
    }
}