package com.acowg.peer.services.locks;

import com.acowg.peer.config.PeerScrapingConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
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
    private final PeerScrapingConfig peerScrapingConfig;

    @Value("${peer.locks.per.drive:1}")
    private int locksPerDrive;

    /**
     * Creates a DriveLockManager with configuration-based paths.
     *
     * @param peerScrapingConfig Configuration containing media paths
     */
    public DriveLockManagerImpl(PeerScrapingConfig peerScrapingConfig) {
        this.peerScrapingConfig = peerScrapingConfig;
    }

    @PostConstruct
    public void initialize() {
        peerScrapingConfig.getDriveToCategories().keySet().forEach(drive -> {
            driveLocks.put(drive, new Semaphore(locksPerDrive));
            log.info("Initialized {} locks for drive: {}", locksPerDrive, drive);
        });
    }

    @Override
    public Semaphore getLockForDrive(String driveLetter) {
        return driveLocks.get(driveLetter);
    }
}