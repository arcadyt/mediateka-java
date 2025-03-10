package com.acowg.peer.mappers;

import com.acowg.peer.entities.DirectoryEntity;
import com.acowg.peer.entities.MediaEntity;
import com.acowg.peer.events.ScrapedFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IScrapeResultMapper {
    @Mapping(target = "directory", source = "directory")
    @Mapping(target = "relativeFilePath", source = "scrapedFile.relativeFilePath")
    @Mapping(target = "sizeInBytes", source = "scrapedFile.sizeInBytes")
    @Mapping(target = "id", ignore = true)
    MediaEntity toMediaEntity(ScrapedFile scrapedFile, DirectoryEntity directory);
}