package com.acowg.catalog.dao.collections;

import com.acowg.catalog.dao.Media;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Data
@Node
public abstract class MediaCollection {
    @Id
    private String id;
    private String name;
    private String description;

    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    private List<Media> mediaItems; // Bi-directional mapping to media
}
