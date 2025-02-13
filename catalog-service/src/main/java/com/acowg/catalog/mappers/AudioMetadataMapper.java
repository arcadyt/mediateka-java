package com.acowg.catalog.mappers;

import com.acowg.catalog.dao.AudioMetadata;
import com.acowg.shared.models.dto.AudioMetadataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AudioMetadataMapper {
    AudioMetadataMapper INSTANCE = Mappers.getMapper(AudioMetadataMapper.class);

    AudioMetadataDTO toDTO(AudioMetadata metadata);

    AudioMetadata toEntity(AudioMetadataDTO metadataDTO);
}
