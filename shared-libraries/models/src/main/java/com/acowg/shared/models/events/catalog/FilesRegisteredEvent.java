package com.acowg.shared.models.events.catalog;

import com.acowg.shared.models.events.AEvent;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * Represents an event where files are registered with peers.
 * This event maps peer IDs to the file IDs they are associated with.
 */
@Getter
@Builder(builderClassName = "Bob")
public class FilesRegisteredEvent extends AEvent {

    /**
     * A mapping of peer IDs to the sets of file IDs associated with those peers.
     */
    Map<String, Set<String>> peerToFiles;
}
