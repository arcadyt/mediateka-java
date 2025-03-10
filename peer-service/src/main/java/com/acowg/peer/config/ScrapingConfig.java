package com.acowg.peer.config;

import com.acowg.peer.entities.DirectoryEntity;
import com.acowg.peer.events.DirectoriesChangeEvent;
import com.acowg.peer.repositories.IDirectoryRepository;
import com.acowg.peer.utils.PathUtils;
import com.acowg.shared.models.enums.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScrapingConfig {
    private final IDirectoryRepository directoryRepository;
    private final AtomicReference<Map<String, List<CategoryToRootPath>>> driveToCategories =
            new AtomicReference<>();

    public record CategoryToRootPath(CategoryType category, File rootPath) {}

    /**
     * Gets the current mapping of drives to category-path pairs.
     * Uses cached data if available, otherwise fetches fresh data.
     */
    public Map<String, List<CategoryToRootPath>> getDriveToCategories() {
        Map<String, List<CategoryToRootPath>> currentMapping = driveToCategories.get();
        if (currentMapping == null) {
            refreshMappings();
            return driveToCategories.get();
        }
        return currentMapping;
    }

    /**
     * Forces a refresh of the drive-to-categories mapping.
     * This will query the database for the latest directory configurations.
     */
    public void refreshMappings() {
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

        driveToCategories.set(mapping);
        log.debug("Drive to categories mapping refreshed, now has {} drives", mapping.size());
    }

    /**
     * Event listener that refreshes the configuration when directories change.
     */
    @EventListener
    public void handleDirectoryChangeEvent(DirectoriesChangeEvent event) {
        log.info("Directory change detected: {} - {}. Refreshing scraping configuration.",
                event.getChangeType(), event.getDirectory().getPath());

        refreshMappings();

        log.debug("Scraping configuration refreshed after directory change");
    }
}