package com.acowg.catalog.mappers;

import com.acowg.catalog.dao.Video;
import com.acowg.shared.models.dto.VideoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {VideoMetadataMapper.class})
public interface VideoMapper {
    VideoMapper INSTANCE = Mappers.getMapper(VideoMapper.class);

    VideoDTO toDTO(Video video);

    Video toEntity(VideoDTO videoDTO);
}
