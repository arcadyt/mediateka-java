package com.acowg.peer.mappers;

import com.acowg.peer.entities.MediaEntity;
import com.acowg.proto.peer_edge.PeerEdge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IMediaFileMapper {
    
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
}