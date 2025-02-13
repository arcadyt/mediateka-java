package com.acowg.catalog.mappers;

import com.acowg.catalog.dao.Audio;
import com.acowg.shared.models.dto.AudioDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {AudioMetadataMapper.class})
public interface AudioMapper {
    AudioMapper INSTANCE = Mappers.getMapper(AudioMapper.class);

    AudioDTO toDTO(Audio audio);

    Audio toEntity(AudioDTO audioDTO);
}
