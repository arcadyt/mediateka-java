package com.acowg.catalog.dao.collections;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Data
@Node
@EqualsAndHashCode(callSuper = true)
public class Series extends MediaCollection {
    private List<Season> seasons;
}
