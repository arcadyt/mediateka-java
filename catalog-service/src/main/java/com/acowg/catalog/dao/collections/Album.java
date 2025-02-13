package com.acowg.catalog.dao.collections;

import com.acowg.catalog.dao.Audio;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Data
@Node
@EqualsAndHashCode(callSuper = true)
public class Album extends MediaCollection {
    private List<Audio> tracks;
}
