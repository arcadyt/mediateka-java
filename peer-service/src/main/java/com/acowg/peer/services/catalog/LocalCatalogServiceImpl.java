package com.acowg.peer.services.catalog;

import com.acowg.peer.entities.DirectoryEntity;
import com.acowg.peer.entities.HasMediaOfferingFields;
import com.acowg.peer.entities.MediaEntity;
import com.acowg.peer.events.CatalogUpdateResult;
import com.acowg.peer.events.ScrapeResultEvent;
import com.acowg.peer.events.ScrapedFile;
import com.acowg.peer.mappers.IScrapeResultMapper;
import com.acowg.peer.repositories.IDirectoryRepository;
import com.acowg.peer.repositories.IMediaRepository;
import com.acowg.shared.models.enums.CategoryType;
import com.google.common.collect.Sets;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalCatalogServiceImpl implements ILocalCatalogService {
    private final IMediaRepository mediaRepository;
    private final IDirectoryRepository directoryRepository;
    private final IScrapeResultMapper scrapeResultMapper;

    @Transactional
    @Override
    public void deleteByCatalogId(String catalogId) {
        mediaRepository.deleteByCatalogId(catalogId);
    }

    @Transactional
    @Override
    public void deleteByCatalogUUIDs(Set<String> catalogIds) {
        mediaRepository.deleteByCatalogIdIn(catalogIds);
    }

    @Transactional
    @Override
    public void deleteMediaByLUIDs(Set<String> luids) {
        mediaRepository.deleteAllById(luids);
    }

    @Transactional
    @Override
    public void updateCatalogId(String luid, String newCatalogId) {
        MediaEntity entity = mediaRepository.findById(luid)
                .orElseThrow(() -> new EntityNotFoundException("Media file not found with ID: " + luid));
        entity.setCatalogId(newCatalogId);
        mediaRepository.save(entity);
    }

    @Transactional
    @Override
    public void updateCatalogIds(Map<String, String> luidToCatalogIdMap) {
        Set<String> luids = luidToCatalogIdMap.keySet();
        List<MediaEntity> entities = mediaRepository.findAllById(luids);

        entities.forEach(entity ->
                entity.setCatalogId(luidToCatalogIdMap.get(entity.getId()))
        );

        mediaRepository.saveAll(entities);
    }

    @Override
    public Optional<MediaEntity> findByCatalogId(String catalogId) {
        return mediaRepository.findByCatalogId(catalogId);
    }

    @Transactional
    @Override
    public void deleteByCategoryRootPathAndRelativeFilePathsNotIn(String categoryRootPath, Set<String> relativeFilePaths) {
        mediaRepository.deleteByDirectoryPathAndRelativeFilePathsNotIn(categoryRootPath, relativeFilePaths);
    }

    @Override
    public Page<HasMediaOfferingFields> findUnOfferedMedia(Pageable pageable) {
        return mediaRepository.findByCatalogIdIsNull(pageable);
    }

    @Override
    public Set<String> findRegisteredMedia() {
        return mediaRepository.findAllCatalogIdNotNull();
    }

    @Override
    public Path getFullMediaPath(MediaEntity mediaFile) {
        String directoryPath = mediaFile.getDirectory().getPath();
        String relativeFilePath = mediaFile.getRelativeFilePath();
        return Paths.get(directoryPath, relativeFilePath);
    }

    @Transactional
    @Override
    public CatalogUpdateResult createMissingAndDeleteObsoleteFiles(ScrapeResultEvent event) {
        // 1. Find or create the directory entity
        DirectoryEntity directory = getOrCreateDirectory(event.getBaseDirectoryPath(), event.getCategory());

        // 2. Extract relative file paths from the event
        Set<String> currentFilePaths = event.getScrapedFiles().stream()
                .map(ScrapedFile::relativeFilePath)
                .collect(Collectors.toSet());

        // 2.1. Query for catalog IDs of files that will be removed (using a single database query)
        Set<String> removedCatalogIds = mediaRepository.findCatalogIdsByDirectoryPathAndRelativeFilePathsNotIn(
                directory.getPath(), currentFilePaths);

        // 3. Delete files that no longer exist on disk (only if we found some)
        if (!removedCatalogIds.isEmpty()) {
            mediaRepository.deleteByDirectoryPathAndRelativeFilePathsNotIn(
                    directory.getPath(), currentFilePaths);
        }

        // 4. Get ALL existing paths in the database for this directory
        Set<String> allExistingPaths = mediaRepository.findRelativePathsByDirectoryPath(directory.getPath());

        // 5. Calculate truly missing paths (those that don't exist in the database at all)
        Set<String> missingPaths = Sets.difference(currentFilePaths, allExistingPaths);

        // 6. Add only new files (those that are missing in the database)
        Set<MediaEntity> newMediaEntities = event.getScrapedFiles().stream()
                .filter(file -> missingPaths.contains(file.relativeFilePath()))
                .map(scrapedFile -> scrapeResultMapper.toMediaEntity(scrapedFile, directory))
                .collect(Collectors.toSet());

        mediaRepository.saveAll(newMediaEntities);

        return new CatalogUpdateResult(removedCatalogIds, newMediaEntities);
    }

    private DirectoryEntity getOrCreateDirectory(String path, CategoryType categoryType) {
        return directoryRepository.findByPath(path)
                .orElseGet(() -> {
                    DirectoryEntity newDirectory = new DirectoryEntity();
                    newDirectory.setPath(path);
                    newDirectory.setDefaultCategory(categoryType);
                    newDirectory.setMediaFiles(new HashSet<>());
                    return directoryRepository.save(newDirectory);
                });
    }
}