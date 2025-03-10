package com.acowg.peer.services.locks;

import com.acowg.peer.config.ScrapingConfig;
import com.acowg.peer.events.DirectoriesChangeEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Manages locks for each drive, allowing parallel control of operations based on drive letters.
 */
@Slf4j
@Component
public class DriveLockManagerImpl implements IDriveLockManager {

    private final Map<String, Semaphore> driveLocks = new HashMap<>();
    private final ScrapingConfig scrapingConfig;

    @Value("${peer.locks.per.drive:1}")
    private int locksPerDrive;

    /**
     * Creates a DriveLockManager with configuration-based paths.
     *
     * @param scrapingConfig Configuration containing media paths
     */
    public DriveLockManagerImpl(ScrapingConfig scrapingConfig) {
        this.scrapingConfig = scrapingConfig;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing drive locks...");
        // Clear existing locks first
        driveLocks.clear();

        scrapingConfig.getDriveToCategories().keySet().forEach(drive -> {
            driveLocks.put(drive, new Semaphore(locksPerDrive));
            log.info("Initialized {} locks for drive: {}", locksPerDrive, drive);
        });
    }

    @Override
    public Semaphore getLockForDrive(String driveLetter) {
        return driveLocks.get(driveLetter);
    }

    @EventListener
    public void handleDirectoriesChangeEvent(DirectoriesChangeEvent event) {
        log.info("Directory change detected ({}): {}. Reinitializing drive locks.",
                event.getChangeType(),
                event.getDirectory().getPath());
        initialize();
    }
}