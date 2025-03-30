package com.acowg.peer.mappers;

import com.acowg.peer.dto.DirectoryDto;
import com.acowg.peer.entities.DirectoryEntity;
import com.acowg.shared.models.enums.CategoryType;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IDirectoryMapper {

    @Mapping(target = "mediaFiles", ignore = true)
    DirectoryDto toDto(DirectoryEntity entity);

    List<DirectoryDto> toDtoList(List<DirectoryEntity> entities);

    DirectoryEntity toEntity(DirectoryDto dto);
}