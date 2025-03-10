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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectoryService {
    private final IDirectoryRepository directoryRepository;
    private final IMediaRepository mediaRepository;
    private final IDirectoryMapper directoryMapper;
    private final IMediaMapper mediaMapper;

    public List<DirectoryDto> getAllDirectories(boolean slim) {
        List<DirectoryEntity> directories = directoryRepository.findAll();
        return slim ?
                directoryMapper.toSlimDtoList(directories) :
                directoryMapper.toDtoList(directories);
    }

    public DirectoryDto getDirectoryById(String id, boolean slim) {
        DirectoryEntity directory = directoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Directory not found with id: " + id));

        return slim ?
                directoryMapper.toSlimDto(directory) :
                directoryMapper.toDto(directory);
    }

    public Optional<DirectoryDto> findDirectoryById(String id, boolean slim) {
        return directoryRepository.findById(id)
                .map(entity -> slim ?
                        directoryMapper.toSlimDto(entity) :
                        directoryMapper.toDto(entity));
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
        DirectoryEntity entity = directoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Directory not found with id: " + id));

        directoryRepository.deleteById(id);
    }

    public List<MediaDto> getMediaByDirectoryId(String id) {
        DirectoryEntity directory = directoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Directory not found with id: " + id));

        List<MediaEntity> mediaEntities = mediaRepository.findByDirectoryId(id);
        return mediaMapper.toDtoList(mediaEntities);
    }
}