package com.acowg.peer.mappers;

import com.acowg.peer.entities.DirectoryEntity;
import com.acowg.shared.models.enums.CategoryType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DirectoryMapper {
    DirectoryEntity fromEvent(String categoryRoot, CategoryType defaultCategory);
}