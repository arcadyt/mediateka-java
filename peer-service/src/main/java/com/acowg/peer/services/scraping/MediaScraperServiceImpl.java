package com.acowg.peer.services.scraping;

import com.acowg.peer.config.PeerScrapingConfig;
import com.acowg.peer.events.ScrapeResultEvent;
import com.acowg.peer.events.ScrapedFile;
import com.acowg.peer.services.locks.RequiresDriveLock;
import com.acowg.shared.models.enums.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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

    @RequiresDriveLock(pathParamName = "categoryRoot")
    @Override
    public void scanCategoryDirectory(CategoryType categoryType, File categoryRoot) {
        Path categoryPath = categoryRoot.toPath();
        log.info("Scanning category '{}' under path '{}'", categoryType, categoryPath);

        Set<ScrapedFile> scrapedFiles = fileScanner.scanDirectory(categoryPath);
        ScrapeResultEvent event = new ScrapeResultEvent(
                this.getClass(),
                categoryRoot.toString(),
                categoryType,
                scrapedFiles
        );

        eventPublisher.publishEvent(event);
    }
}