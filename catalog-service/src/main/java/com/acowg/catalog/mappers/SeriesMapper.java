package com.acowg.catalog.mappers;

import com.acowg.catalog.dao.collections.Series;
import com.acowg.shared.models.dto.collections.SeriesDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {SeasonMapper.class})
public interface SeriesMapper {
    SeriesMapper INSTANCE = Mappers.getMapper(SeriesMapper.class);

    SeriesDTO toDTO(Series series);

    Series toEntity(SeriesDTO seriesDTO);
}
