package com.acowg.peer.services.locks;

import java.util.concurrent.Semaphore;

public interface IDriveLockManager {
    /**
     * Retrieves the semaphore for a specific drive.
     *
     * @param driveLetter The drive letter for which to retrieve the lock.
     * @return The semaphore for the drive, or null if no lock exists for the drive.
     */
    Semaphore getLockForDrive(String driveLetter);
}