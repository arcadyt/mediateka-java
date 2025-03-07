package com.acowg.catalog.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@Node
@EqualsAndHashCode(callSuper = true)
public class Video extends Media {
    private VideoMetadata metadata; // Specific to video
}
