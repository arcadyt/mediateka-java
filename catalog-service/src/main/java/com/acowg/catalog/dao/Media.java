package com.acowg.catalog.dao;

import com.acowg.shared.models.enums.CategoryType;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Data
@Node
public abstract class Media {

    @Id
    private String id;
    private String title;
    private String description;

    @Relationship(type = "HAS_TAG", direction = Relationship.Direction.OUTGOING)
    private List<String> tags; // Used for searching by tags

    @Relationship(type = "HAS_CAST", direction = Relationship.Direction.OUTGOING)
    private List<String> cast; // Used for searching by cast

    private CategoryType category; // Enum for category
    private AbstractMetadata metadata; // Abstract metadata specific to type
}
