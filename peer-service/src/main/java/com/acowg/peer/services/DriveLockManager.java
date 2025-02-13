package com.acowg.peer.services;

import com.acowg.peer.utils.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import org.springframework.beans.factory.annotation.Value;

/**
 * Manages locks for each drive, allowing parallel control of operations based on drive letters.
 */
@Slf4j
@Component
public class DriveLockManager {

    private final Map<String, Semaphore> driveLocks = new HashMap<>();

    @Value("${peer.locks.per.drive:1}")
    private int locksPerDrive;

    /**
     * Initializes drive locks based on the provided media paths and lock quantities per drive.
     *
     * @param mediaPaths List of media paths from application configuration.
     */
    public DriveLockManager(List<String> mediaPaths) {
        initializeDriveLocks(mediaPaths);
    }

    /**
     * Retrieves the semaphore for a specific drive.
     *
     * @param driveLetter The drive letter for which to retrieve the lock.
     * @return The semaphore for the drive, or null if no lock exists for the drive.
     */
    public Semaphore getLockForDrive(String driveLetter) {
        return driveLocks.get(driveLetter);
    }

    /**
     * Initializes the lock map for all unique drives found in the media paths.
     *
     * @param mediaPaths List of media paths to deduce drive letters from.
     */
    private void initializeDriveLocks(List<String> mediaPaths) {
        mediaPaths.stream()
                .map(Path::of)
                .map(PathUtils::extractRootIdentifier)
                .distinct()
                .forEach(drive -> {
                    driveLocks.put(drive, new Semaphore(locksPerDrive));
                    log.info("Initialized {} locks for drive: {}", locksPerDrive, drive);
                });
    }
}
