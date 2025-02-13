package com.acowg.catalog.mappers;

import com.acowg.catalog.dao.collections.Album;
import com.acowg.shared.models.dto.collections.AlbumDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {AudioMapper.class})
public interface AlbumMapper {
    AlbumMapper INSTANCE = Mappers.getMapper(AlbumMapper.class);

    AlbumDTO toDTO(Album album);

    Album toEntity(AlbumDTO albumDTO);
}
