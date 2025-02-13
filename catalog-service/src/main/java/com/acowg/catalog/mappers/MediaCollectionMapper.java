package com.acowg.catalog.mappers;

import com.acowg.catalog.dao.collections.MediaCollection;
import com.acowg.shared.models.dto.collections.MediaCollectionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {MediaMapper.class})
public interface MediaCollectionMapper {
    MediaCollectionMapper INSTANCE = Mappers.getMapper(MediaCollectionMapper.class);

    MediaCollectionDTO toDTO(MediaCollection mediaCollection);

    MediaCollection toEntity(MediaCollectionDTO mediaCollectionDTO);
}
