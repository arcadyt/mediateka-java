package com.acowg.peer.controllers;

import com.acowg.peer.dto.DirectoryDto;
import com.acowg.peer.dto.MediaDto;
import com.acowg.peer.services.catalog.DirectoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/directories")
@RequiredArgsConstructor
public class DirectoryController {
    private final DirectoryService directoryService;

    @GetMapping
    public List<DirectoryDto> getAllDirectories() {
        return directoryService.getAllDirectories();
    }

    @GetMapping("/{id}")
    public DirectoryDto getDirectoryById(@PathVariable(name = "id") String id) {
        try {
            return directoryService.getDirectoryById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<DirectoryDto> createDirectory(@RequestBody DirectoryDto directoryDto) {
        DirectoryDto createdDirectory = directoryService.createDirectory(directoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDirectory);
    }

    @PutMapping("/{id}")
    public DirectoryDto updateDirectory(
            @PathVariable(name = "id") String id,
            @RequestBody DirectoryDto directoryDto) {
        try {
            return directoryService.updateDirectory(id, directoryDto);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDirectory(@PathVariable(name = "id") String id) {
        try {
            directoryService.deleteDirectory(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/media")
    public List<MediaDto> getMediaByDirectoryId(@PathVariable(name = "id") String id) {
        try {
            log.debug("Fetching media for directory ID: {}", id);
            return directoryService.getMediaByDirectoryId(id);
        } catch (Exception e) {
            log.error("Error fetching media for directory ID: {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error fetching media: " + e.getMessage());
        }
    }
}