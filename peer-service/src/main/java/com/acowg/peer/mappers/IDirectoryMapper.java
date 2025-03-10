package com.acowg.peer.mappers;

import com.acowg.peer.dto.DirectoryDto;
import com.acowg.peer.entities.DirectoryEntity;
import com.acowg.shared.models.enums.CategoryType;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IDirectoryMapper {
    DirectoryEntity fromEvent(String categoryRoot, CategoryType defaultCategory);

    /**
     * Maps entity to full DTO with relationships.
     * Uses context to prevent circular references.
     */
    @Named("toDto")
    @Mapping(target = "mediaFiles", source = "mediaFiles")
    DirectoryDto toDto(DirectoryEntity entity, @Context CycleAvoidingMappingContext context);

    /**
     * Overloaded method that creates a context automatically
     */
    default DirectoryDto toDto(DirectoryEntity entity) {
        return toDto(entity, new CycleAvoidingMappingContext());
    }

    @Named("toDtoList")
    default List<DirectoryDto> toDtoList(List<DirectoryEntity> entities) {
        if (entities == null) {
            return null;
        }
        CycleAvoidingMappingContext context = new CycleAvoidingMappingContext();
        return entities.stream()
                .map(entity -> toDto(entity, context))
                .toList();
    }

    @Named("toDtoSet")
    default Set<DirectoryDto> toDtoSet(Set<DirectoryEntity> entities) {
        if (entities == null) {
            return null;
        }
        CycleAvoidingMappingContext context = new CycleAvoidingMappingContext();
        return entities.stream()
                .map(entity -> toDto(entity, context))
                .collect(java.util.stream.Collectors.toSet());
    }

    DirectoryEntity toEntity(DirectoryDto dto);

    /**
     * Maps the entity to a DTO without including related entities.
     * This is useful for avoiding circular references.
     */
    @Named("toSlimDto")
    @Mapping(target = "mediaFiles", ignore = true)
    DirectoryDto toSlimDto(DirectoryEntity entity);

    @Named("toSlimDtoList")
    List<DirectoryDto> toSlimDtoList(List<DirectoryEntity> entities);

    @Named("toSlimDtoSet")
    Set<DirectoryDto> toSlimDtoSet(Set<DirectoryEntity> entities);

    /**
     * Used by MapStruct to determine if an instance has already been mapped
     */
    @BeforeMapping
    default <T> T getMappedInstance(Object source, @TargetType Class<T> targetType, @Context CycleAvoidingMappingContext context) {
        return context.getMappedInstance(source, targetType);
    }

    /**
     * Used by MapStruct to store a mapped instance
     */
    @AfterMapping
    default void storeMappedInstance(Object source, @MappingTarget Object target, @Context CycleAvoidingMappingContext context) {
        context.storeMappedInstance(source, target);
    }
}