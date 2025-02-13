package com.acowg.catalog.mappers;

import com.acowg.catalog.dao.collections.Season;
import com.acowg.shared.models.dto.collections.SeasonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {VideoMapper.class})
public interface SeasonMapper {
    SeasonMapper INSTANCE = Mappers.getMapper(SeasonMapper.class);

    SeasonDTO toDTO(Season season);

    Season toEntity(SeasonDTO seasonDTO);
}
