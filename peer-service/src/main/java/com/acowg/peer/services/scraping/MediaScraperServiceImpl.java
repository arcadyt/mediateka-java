package com.acowg.peer.services.scraping;

import com.acowg.peer.config.PeerScrapingConfig;
import com.acowg.peer.events.ScrapeResultEvent;
import com.acowg.peer.events.ScrapedFile;
import com.acowg.shared.models.enums.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaScraperServiceImpl implements IMediaScraperService {

    private final PeerScrapingConfig peerScrapingConfig;
    private final IFileScanner fileScanner;
    private final ApplicationEventPublisher eventPublisher;
    private final Executor taskExecutor;

    /**
     * Scheduled method to trigger media scraping at a fixed rate.
     *
     * @implNote Uses fixedRate to start the next execution regardless of the
     *           previous execution's completion, with a configurable interval.
     */
    @Scheduled(fixedRateString = "#{${peer.scraping-frequency-minutes:30} * 60 * 1000}")
    @Override
    public void scrapeNow() {
        peerScrapingConfig.getDriveToCategories().forEach((drive, categoryToRootPaths) ->
                categoryToRootPaths.forEach(categoryToRootPath ->
                        taskExecutor.execute(() ->
                                scanCategoryDirectory(categoryToRootPath.category(), categoryToRootPath.rootPath())
                        )
                )
        );
    }

    /**
     * Scans a directory for a specific category and publishes the results as an event.
     *
     * @param categoryType The category type to scan.
     * @param categoryRoot The root directory of the category.
     */
    private void scanCategoryDirectory(CategoryType categoryType, File categoryRoot) {
        try {
            Path categoryPath = categoryRoot.toPath();
            Set<ScrapedFile> scrapedFiles = fileScanner.scanDirectory(categoryPath);

            ScrapeResultEvent event = new ScrapeResultEvent(
                    this.getClass(),
                    categoryRoot.toString(),
                    categoryType,
                    scrapedFiles
            );

            eventPublisher.publishEvent(event);

            log.info("Completed scan for category: {} - Found {} files", categoryType, scrapedFiles.size());
        } catch (Exception e) {
            log.error("Error scanning category directory: {}", categoryType, e);
        }
    }
}