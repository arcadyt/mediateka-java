package com.acowg.peer.services.catalog;

import com.acowg.peer.dto.DirectoryDto;
import com.acowg.peer.dto.MediaDto;
import com.acowg.peer.entities.DirectoryEntity;
import com.acowg.peer.entities.MediaEntity;
import com.acowg.peer.mappers.IDirectoryMapper;
import com.acowg.peer.mappers.IMediaMapper;
import com.acowg.peer.repositories.IDirectoryRepository;
import com.acowg.peer.repositories.IMediaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryService {
    private final IDirectoryRepository directoryRepository;
    private final IMediaRepository mediaRepository;
    private final IDirectoryMapper directoryMapper;
    private final IMediaMapper mediaMapper;

    @Transactional(readOnly = true)
    public List<DirectoryDto> getAllDirectories() {
        List<DirectoryEntity> directories = directoryRepository.findAll();
        return directoryMapper.toDtoList(directories);
    }

    @Transactional(readOnly = true)
    public DirectoryDto getDirectoryById(String id) {
        DirectoryEntity directory = directoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Directory not found with id: " + id));
        return directoryMapper.toDto(directory);
    }

    public Optional<DirectoryDto> findDirectoryById(String id) {
        return directoryRepository.findById(id)
                .map(directoryMapper::toDto);
    }

    @Transactional
    public DirectoryDto createDirectory(DirectoryDto directoryDto) {
        DirectoryEntity entity = directoryMapper.toEntity(directoryDto);
        DirectoryEntity savedEntity = directoryRepository.save(entity);
        return directoryMapper.toDto(savedEntity);
    }

    @Transactional
    public DirectoryDto updateDirectory(String id, DirectoryDto directoryDto) {
        if (!directoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Directory not found with id: " + id);
        }

        DirectoryEntity entity = directoryMapper.toEntity(directoryDto);
        entity.setId(id);
        DirectoryEntity savedEntity = directoryRepository.save(entity);
        return directoryMapper.toDto(savedEntity);
    }

    @Transactional
    public void deleteDirectory(String id) {
        if (!directoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Directory not found with id: " + id);
        }
        directoryRepository.deleteById(id);
    }

    /**
     * Get media files for a directory using the repository method directly
     */
    @Transactional(readOnly = true)
    public List<MediaDto> getMediaByDirectoryId(String id) {
        // Verify directory exists first
        if (!directoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Directory not found with id: " + id);
        }

        try {
            // Use the repository method directly
            List<MediaEntity> mediaList = mediaRepository.findByDirectoryId(id);

            // Map to DTOs
            return mediaMapper.toDtoList(mediaList);
        } catch (Exception e) {
            log.error("Error retrieving media for directory {}: {}", id, e.getMessage());
            // Return empty list instead of throwing error
            return new ArrayList<>();
        }
    }
}