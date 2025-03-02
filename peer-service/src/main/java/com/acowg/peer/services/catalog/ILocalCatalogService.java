package com.acowg.peer.services.catalog;

import com.acowg.peer.entities.HasMediaOfferingFields;
import com.acowg.peer.entities.MediaEntity;
import com.acowg.peer.events.CatalogUpdateResult;
import com.acowg.peer.events.ScrapeResultEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ILocalCatalogService {
    void deleteByCatalogId(String catalogId);

    void deleteByCatalogUUIDs(Set<String> catalogIds);

    void deleteMediaByLUIDs(Set<String> luids);

    void updateCatalogId(String luid, String newCatalogId);

    void updateCatalogIds(Map<String, String> luidToCatalogIdMap);

    Page<HasMediaOfferingFields> findUnOfferedMedia(Pageable pageable);

    Set<String> findRegisteredMedia();

    Optional<MediaEntity> findByCatalogId(String catalogId);

    void deleteByCategoryRootPathAndRelativeFilePathsNotIn(String categoryRootPath, Set<String> relativeFilePaths);

    Path getFullMediaPath(MediaEntity mediaFile);

    CatalogUpdateResult createMissingAndDeleteObsoleteFiles(ScrapeResultEvent scrapeResult);
}
