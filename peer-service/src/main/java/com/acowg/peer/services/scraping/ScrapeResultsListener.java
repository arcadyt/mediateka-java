package com.acowg.peer.services.scraping;

import com.acowg.peer.events.CatalogUpdateResult;
import com.acowg.peer.events.ScrapeResultEvent;
import com.acowg.peer.services.catalog.ILocalCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScrapeResultsListener implements ApplicationListener<ScrapeResultEvent> {
    private ILocalCatalogService catalogService;

    @Override
    public void onApplicationEvent(ScrapeResultEvent scrapeResult) {
        CatalogUpdateResult result = catalogService.createMissingAndDeleteObsoleteFiles(scrapeResult);
    }

}