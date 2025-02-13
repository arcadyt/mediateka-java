package com.acowg.peer.kafka;

import com.acowg.shared.models.events.peer.FilesScrapedEvent;

public interface MediaPublisher {
    void publishBatch(FilesScrapedEvent event);
}
