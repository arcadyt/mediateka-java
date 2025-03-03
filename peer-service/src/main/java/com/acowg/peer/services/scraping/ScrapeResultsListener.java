package com.acowg.peer.services.scraping;

import com.acowg.peer.events.CatalogUpdateResult;
import com.acowg.peer.events.ScrapeResultEvent;
import com.acowg.peer.grpc.PeerEdgeClient;
import com.acowg.peer.mappers.IMediaFileMapper;
import com.acowg.peer.services.catalog.ILocalCatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScrapeResultsListener implements ApplicationListener<ScrapeResultEvent> {
    private final ILocalCatalogService catalogService;
    private final PeerEdgeClient peerEdgeClient;
    private final IMediaFileMapper mediaFileMapper;

    @Override
    public void onApplicationEvent(@NonNull ScrapeResultEvent scrapeResult) {
        CatalogUpdateResult result = catalogService.createMissingAndDeleteObsoleteFiles(scrapeResult);
        var offerItems = mediaFileMapper.toFileOfferItems(result.newMediaOffers());
        peerEdgeClient.sendBatchFileOffer(offerItems);

        peerEdgeClient.sendDeletedFilesNotification(result.deletedCatalogIds());

        log.info("Processed scan result for {} with {} new files and {} removed catalog IDs",
                scrapeResult.getBaseDirectoryPath(),
                result.newMediaOffers().size(),
                result.deletedCatalogIds().size());
    }
}