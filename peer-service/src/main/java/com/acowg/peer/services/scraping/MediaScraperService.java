package com.acowg.peer.services.scraping;

import com.acowg.peer.config.PeerScrapingConfig;
import com.acowg.peer.events.ScrapeResultEvent;
import com.acowg.peer.events.ScrapedFile;
import com.acowg.peer.services.DriveLockManager;
import com.acowg.shared.models.enums.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaScraperService {

    private final PeerScrapingConfig peerScrapingConfig;
    private final DriveLockManager driveLockManager;
    private final IFileScanner fileScanner;
    private final ApplicationEventPublisher eventPublisher;
    private final Executor taskExecutor; // Injected thread pool

    /**
     * Scrapes all configured categories across all drives in parallel.
     */
    public void scrape() {
        peerScrapingConfig.getDriveToCategories().forEach((drive, categoryToRootPaths) ->
                categoryToRootPaths.forEach(categoryToRootPath ->
                        taskExecutor.execute(() -> scrapeCategoryRoot(drive, categoryToRootPath.category(), categoryToRootPath.rootPath()))
                )
        );
    }

    /**
     * Scrapes a specific category root.
     */
    private void scrapeCategoryRoot(String drive, CategoryType categoryType, File categoryRoot) {
        Path categoryPath = categoryRoot.toPath();
        Semaphore lock = driveLockManager.getLockForDrive(drive);

        if (lock == null) {
            log.warn("No lock found for drive: {}, category: {}, path: {}", drive, categoryType, categoryPath);
            return;
        }

        try {
            lock.acquire();
            log.info("Scanning category '{}' under path '{}'", categoryType, categoryPath);

            Set<ScrapedFile> scrapedFiles = fileScanner.scanDirectory(categoryPath);
            ScrapeResultEvent event = new ScrapeResultEvent(
                    this.getClass(),
                    categoryRoot.toString(),
                    categoryType,
                    scrapedFiles
            );

            eventPublisher.publishEvent(event);
        } catch (InterruptedException e) {
            log.error("Thread interrupted while acquiring lock for '{}'", categoryPath, e);
            Thread.currentThread().interrupt();
        } finally {
            lock.release();
        }
    }
}
