package com.acowg.peer.services.catalog;

import com.acowg.peer.dto.MediaDto;
import com.acowg.peer.entities.HasMediaOfferingFields;
import com.acowg.peer.entities.MediaEntity;
import com.acowg.peer.mappers.IMediaMapper;
import com.acowg.peer.repositories.IMediaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final IMediaRepository mediaRepository;
    private final IMediaMapper mediaMapper;

    @Transactional(readOnly = true)
    public List<MediaDto> getAllMedia() {
        List<MediaEntity> mediaEntities = mediaRepository.findAll();
        return mediaMapper.toDtoList(mediaEntities);
    }

    @Transactional(readOnly = true)
    public MediaDto getMediaById(String id) {
        MediaEntity mediaEntity = mediaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Media not found with id: " + id));

        return mediaMapper.toDto(mediaEntity);
    }

    @Transactional(readOnly = true)
    public Optional<MediaDto> findMediaById(String id) {
        return mediaRepository.findById(id)
                .map(mediaMapper::toDto);
    }

    @Transactional
    public MediaDto createMedia(MediaDto mediaDto) {
        MediaEntity entity = mediaMapper.toEntity(mediaDto);
        MediaEntity savedEntity = mediaRepository.save(entity);
        return mediaMapper.toDto(savedEntity);
    }

    @Transactional
    public MediaDto updateMedia(String id, MediaDto mediaDto) {
        if (!mediaRepository.existsById(id)) {
            throw new EntityNotFoundException("Media not found with id: " + id);
        }

        MediaEntity entity = mediaMapper.toEntity(mediaDto);
        entity.setId(id);
        MediaEntity savedEntity = mediaRepository.save(entity);
        return mediaMapper.toDto(savedEntity);
    }

    @Transactional
    public void deleteMedia(String id) {
        if (!mediaRepository.existsById(id)) {
            throw new EntityNotFoundException("Media not found with id: " + id);
        }

        mediaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<MediaDto> getMediaWithoutCatalogId(Pageable pageable) {
        Page<HasMediaOfferingFields> mediaPage = mediaRepository.findByCatalogIdIsNull(pageable);

        List<MediaDto> mediaDtos = mediaPage.getContent().stream()
                .filter(field -> field instanceof MediaEntity)
                .map(field -> mediaMapper.toDto((MediaEntity) field))
                .collect(Collectors.toList());

        return new PageImpl<>(mediaDtos, pageable, mediaPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public MediaDto getMediaByCatalogId(String catalogId) {
        MediaEntity mediaEntity = mediaRepository.findByCatalogId(catalogId)
                .orElseThrow(() -> new EntityNotFoundException("Media not found with catalog id: " + catalogId));

        return mediaMapper.toDto(mediaEntity);
    }

    @Transactional(readOnly = true)
    public Optional<MediaDto> findMediaByCatalogId(String catalogId) {
        return mediaRepository.findByCatalogId(catalogId)
                .map(mediaMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Set<String> getRegisteredMediaCatalogIds() {
        return mediaRepository.findAllCatalogIdNotNull();
    }

    @Transactional
    public void deleteByCatalogId(String catalogId) {
        mediaRepository.deleteByCatalogId(catalogId);
    }

    @Transactional
    public void deleteByCatalogIds(Set<String> catalogIds) {
        mediaRepository.deleteByCatalogIdIn(catalogIds);
    }
}