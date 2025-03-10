package com.acowg.peer.controllers;

import com.acowg.peer.dto.MediaDto;
import com.acowg.peer.services.catalog.MediaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @GetMapping
    public List<MediaDto> getAllMedia(
            @RequestParam(value = "slim", defaultValue = "false") boolean slim) {
        return mediaService.getAllMedia(slim);
    }

    @GetMapping("/{id}")
    public MediaDto getMediaById(
            @PathVariable String id,
            @RequestParam(value = "slim", defaultValue = "false") boolean slim) {
        try {
            return mediaService.getMediaById(id, slim);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<MediaDto> createMedia(@RequestBody MediaDto mediaDto) {
        MediaDto createdMedia = mediaService.createMedia(mediaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMedia);
    }

    @PutMapping("/{id}")
    public MediaDto updateMedia(
            @PathVariable String id,
            @RequestBody MediaDto mediaDto) {
        try {
            return mediaService.updateMedia(id, mediaDto);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable String id) {
        try {
            mediaService.deleteMedia(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/uncataloged")
    public Page<MediaDto> getUncatalogedMedia(
            Pageable pageable,
            @RequestParam(value = "slim", defaultValue = "false") boolean slim) {
        return mediaService.getMediaWithoutCatalogId(pageable, slim);
    }

    @GetMapping("/catalogId/{catalogId}")
    public MediaDto getMediaByCatalogId(
            @PathVariable String catalogId,
            @RequestParam(value = "slim", defaultValue = "false") boolean slim) {
        try {
            return mediaService.getMediaByCatalogId(catalogId, slim);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/registered")
    public Set<String> getRegisteredMediaCatalogIds() {
        return mediaService.getRegisteredMediaCatalogIds();
    }

    @DeleteMapping("/catalogId/{catalogId}")
    public ResponseEntity<Void> deleteMediaByCatalogId(@PathVariable String catalogId) {
        mediaService.deleteByCatalogId(catalogId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batchDelete")
    public ResponseEntity<Void> deleteMediaByCatalogIds(@RequestBody Set<String> catalogIds) {
        mediaService.deleteByCatalogIds(catalogIds);
        return ResponseEntity.noContent().build();
    }
}