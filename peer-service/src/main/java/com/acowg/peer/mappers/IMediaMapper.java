package com.acowg.peer.mappers;

import com.acowg.peer.dto.MediaDto;
import com.acowg.peer.entities.MediaEntity;
import com.acowg.proto.peer_edge.PeerEdge;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IMediaMapper {

    @Mapping(target = "directory", ignore = true)
    MediaDto toDto(MediaEntity entity);

    List<MediaDto> toDtoList(List<MediaEntity> entities);

    @Mapping(target = "peerLuid", source = "id")
    @Mapping(target = "relativePath", source = "relativeFilePath")
    @Mapping(target = "sizeBytes", source = "sizeInBytes")
    PeerEdge.FileOfferItem toFileOfferItem(MediaEntity entity);

    default Set<PeerEdge.FileOfferItem> toFileOfferItems(Set<MediaEntity> entities) {
        if (entities == null) {
            return java.util.Collections.emptySet();
        }
        return entities.stream()
                .map(this::toFileOfferItem)
                .collect(java.util.stream.Collectors.toSet());
    }

    MediaEntity toEntity(MediaDto dto);
}