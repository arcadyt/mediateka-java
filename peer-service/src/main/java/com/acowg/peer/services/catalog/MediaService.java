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

    public List<MediaDto> getAllMedia(boolean slim) {
        List<MediaEntity> mediaEntities = mediaRepository.findAll();
        return slim ? 
                mediaMapper.toSlimDtoList(mediaEntities) : 
                mediaMapper.toDtoList(mediaEntities);
    }

    public MediaDto getMediaById(String id, boolean slim) {
        MediaEntity mediaEntity = mediaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Media not found with id: " + id));
        
        return slim ? 
                mediaMapper.toSlimDto(mediaEntity) : 
                mediaMapper.toDto(mediaEntity);
    }

    public Optional<MediaDto> findMediaById(String id, boolean slim) {
        return mediaRepository.findById(id)
                .map(entity -> slim ? 
                        mediaMapper.toSlimDto(entity) : 
                        mediaMapper.toDto(entity));
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

    public Page<MediaDto> getMediaWithoutCatalogId(Pageable pageable, boolean slim) {
        Page<HasMediaOfferingFields> mediaPage = mediaRepository.findByCatalogIdIsNull(pageable);
        
        List<MediaDto> mediaDtos = mediaPage.getContent().stream()
                .filter(field -> field instanceof MediaEntity)
                .map(field -> {
                    MediaEntity entity = (MediaEntity) field;
                    return slim ? 
                            mediaMapper.toSlimDto(entity) : 
                            mediaMapper.toDto(entity);
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(mediaDtos, pageable, mediaPage.getTotalElements());
    }

    public MediaDto getMediaByCatalogId(String catalogId, boolean slim) {
        MediaEntity mediaEntity = mediaRepository.findByCatalogId(catalogId)
                .orElseThrow(() -> new EntityNotFoundException("Media not found with catalog id: " + catalogId));
        
        return slim ? 
                mediaMapper.toSlimDto(mediaEntity) : 
                mediaMapper.toDto(mediaEntity);
    }

    public Optional<MediaDto> findMediaByCatalogId(String catalogId, boolean slim) {
        return mediaRepository.findByCatalogId(catalogId)
                .map(entity -> slim ? 
                        mediaMapper.toSlimDto(entity) : 
                        mediaMapper.toDto(entity));
    }

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