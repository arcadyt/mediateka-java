package com.acowg.catalog.mappers;

import com.acowg.catalog.dao.Media;
import com.acowg.shared.models.dto.MediaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {VideoMapper.class, AudioMapper.class})
public interface MediaMapper {
    MediaMapper INSTANCE = Mappers.getMapper(MediaMapper.class);

    MediaDTO toDTO(Media media);

    Media toEntity(MediaDTO mediaDTO);
}
