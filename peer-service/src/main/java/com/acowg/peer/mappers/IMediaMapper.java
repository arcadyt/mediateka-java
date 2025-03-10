package com.acowg.peer.mappers;

import com.acowg.peer.dto.MediaDto;
import com.acowg.peer.entities.MediaEntity;
import com.acowg.proto.peer_edge.PeerEdge;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IMediaMapper {

    @Mapping(target = "peerLuid", source = "id")
    @Mapping(target = "relativePath", source = "relativeFilePath")
    @Mapping(target = "sizeBytes", source = "sizeInBytes")
    PeerEdge.FileOfferItem toFileOfferItem(MediaEntity entity);

    default Set<PeerEdge.FileOfferItem> toFileOfferItems(Set<MediaEntity> entities) {
        if (entities == null) {
            return Collections.emptySet();
        }
        return entities.stream()
                .map(this::toFileOfferItem)
                .collect(Collectors.toSet());
    }

    /**
     * Maps entity to full DTO with relationships.
     * Uses context to prevent circular references.
     */
    @Named("toDto")
    @Mapping(target = "directory", source = "directory")
    MediaDto toDto(MediaEntity entity, @Context CycleAvoidingMappingContext context);

    /**
     * Overloaded method that creates a context automatically
     */
    default MediaDto toDto(MediaEntity entity) {
        return toDto(entity, new CycleAvoidingMappingContext());
    }

    @Named("toDtoList")
    default List<MediaDto> toDtoList(List<MediaEntity> entities) {
        if (entities == null) {
            return null;
        }
        CycleAvoidingMappingContext context = new CycleAvoidingMappingContext();
        return entities.stream()
                .map(entity -> toDto(entity, context))
                .toList();
    }

    @Named("toDtoSet")
    default Set<MediaDto> toDtoSet(Set<MediaEntity> entities) {
        if (entities == null) {
            return null;
        }
        CycleAvoidingMappingContext context = new CycleAvoidingMappingContext();
        return entities.stream()
                .map(entity -> toDto(entity, context))
                .collect(Collectors.toSet());
    }

    MediaEntity toEntity(MediaDto dto);

    /**
     * Maps the entity to a DTO without including related entities.
     * This is useful for avoiding circular references.
     */
    @Named("toSlimDto")
    @Mapping(target = "directory", ignore = true)
    MediaDto toSlimDto(MediaEntity entity);

    @Named("toSlimDtoList")
    List<MediaDto> toSlimDtoList(List<MediaEntity> entities);

    @Named("toSlimDtoSet")
    Set<MediaDto> toSlimDtoSet(Set<MediaEntity> entities);

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