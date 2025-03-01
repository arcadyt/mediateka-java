package com.acowg.peer.mappers;

import com.acowg.peer.entities.DirectoryEntity;
import com.acowg.peer.entities.MediaEntity;
import com.acowg.peer.events.ScrapedFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScrapeResultMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "catalogId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "baseDirectory", source = "directory")
    MediaEntity toMediaEntity(ScrapedFile scrapedFile, DirectoryEntity directory);
}