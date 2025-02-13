package com.acowg.catalog.dao.collections;

import com.acowg.catalog.dao.Video;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Data
@Node
public class Season {
    private int seasonNumber;
    private List<Video> episodes;
}