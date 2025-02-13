package com.acowg.catalog.mappers;

import com.acowg.catalog.dao.VideoMetadata;
import com.acowg.shared.models.dto.VideoMetadataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VideoMetadataMapper {
    VideoMetadataMapper INSTANCE = Mappers.getMapper(VideoMetadataMapper.class);

    VideoMetadataDTO toDTO(VideoMetadata metadata);

    VideoMetadata toEntity(VideoMetadataDTO metadataDTO);
}
