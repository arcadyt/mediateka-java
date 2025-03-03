package com.acowg.peer.services.scraping;

import com.acowg.peer.events.CatalogUpdateResult;
import com.acowg.peer.events.ScrapeResultEvent;
import com.acowg.peer.grpc.PeerEdgeClient;
import com.acowg.peer.services.catalog.ILocalCatalogService;
import com.acowg.proto.peer_edge.PeerEdge;
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

    @Override
    public void onApplicationEvent(@NonNull ScrapeResultEvent scrapeResult) {
        CatalogUpdateResult result = catalogService.createMissingAndDeleteObsoleteFiles(scrapeResult);

        result.newMediaOffers().forEach(this::sendFileOffer);

        log.info("Processed scan result for {} with {} new files and {} removed catalog IDs",
                scrapeResult.getBaseDirectoryPath(),
                result.newMediaOffers().size(),
                result.deletedCatalogIds().size());
    }

    private void sendFileOffer(PeerEdge.FileOfferRequest fileOffer) {
        try {
            peerEdgeClient.sendFileOffer(
                    fileOffer.getRelativePath(),
                    fileOffer.getSizeBytes()
            );
        } catch (Exception e) {
            log.error("Failed to send file offer for {}: {}",
                    fileOffer.getRelativePath(), e.getMessage(), e);
        }
    }
}