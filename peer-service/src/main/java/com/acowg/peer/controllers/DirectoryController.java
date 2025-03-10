// DirectoryController.java
package com.acowg.peer.controllers;

import com.acowg.peer.dto.DirectoryDto;
import com.acowg.peer.dto.MediaDto;
import com.acowg.peer.services.catalog.DirectoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/directories")
@RequiredArgsConstructor
public class DirectoryController {
    private final DirectoryService directoryService;

    @GetMapping
    public List<DirectoryDto> getAllDirectories(
            @RequestParam(value = "slim", defaultValue = "false") boolean slim) {
        return directoryService.getAllDirectories(slim);
    }

    @GetMapping("/{id}")
    public DirectoryDto getDirectoryById(
            @PathVariable String id,
            @RequestParam(value = "slim", defaultValue = "false") boolean slim) {
        try {
            return directoryService.getDirectoryById(id, slim);
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
            @PathVariable String id,
            @RequestBody DirectoryDto directoryDto) {
        try {
            return directoryService.updateDirectory(id, directoryDto);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDirectory(@PathVariable String id) {
        try {
            directoryService.deleteDirectory(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/media")
    public List<MediaDto> getMediaByDirectoryId(@PathVariable String id) {
        try {
            return directoryService.getMediaByDirectoryId(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}