package com.acowg.peer.services.catalog;

import com.acowg.peer.entities.MediaFileEntity;
import com.acowg.peer.repositories.IMediaFileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements ICatalogService {
    private final IMediaFileRepository mediaFileRepository;

    @Transactional
    public void deleteByCatalogId(String catalogId) {
        mediaFileRepository.deleteByCatalogId(catalogId);
    }

    @Transactional
    public void deleteByCatalogUUIDs(Set<String> catalogIds) {
        mediaFileRepository.deleteByCatalogIdIn(catalogIds);
    }

    @Transactional
    public void deleteMediaByLUIDs(Set<String> luids) {
        mediaFileRepository.deleteAllById(luids);
    }

    @Transactional
    public void updateCatalogId(String luid, String newCatalogId) {
        MediaFileEntity entity = mediaFileRepository.findById(luid)
                .orElseThrow(() -> new EntityNotFoundException("Media file not found with ID: " + luid));
        entity.setCatalogId(newCatalogId);
        mediaFileRepository.save(entity);
    }

    @Transactional
    public void updateCatalogIds(Map<String, String> luidToCatalogIdMap) {
        Set<String> luids = luidToCatalogIdMap.keySet();
        List<MediaFileEntity> entities = mediaFileRepository.findAllById(luids);

        entities.forEach(entity ->
                entity.setCatalogId(luidToCatalogIdMap.get(entity.getId()))
        );

        mediaFileRepository.saveAll(entities);
    }

    public List<MediaFileEntity> findUncataloggedMedia() {
        return mediaFileRepository.findByCatalogIdIsNull();
    }

    public List<MediaFileEntity> findCataloggedMedia() {
        return mediaFileRepository.findByCatalogIdIsNotNull();
    }

    public Path getFullMediaPath(MediaFileEntity mediaFile) {
        String categoryRootPath = mediaFile.getCategoryRoot().getCategoryRootPath();
        String relativeFilePath = mediaFile.getRelativeFilePath();
        return Paths.get(categoryRootPath, relativeFilePath);
    }
}