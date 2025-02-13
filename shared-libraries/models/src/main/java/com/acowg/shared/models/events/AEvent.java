package com.acowg.shared.models.events;

import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class AEvent {
    private final long timestamp = System.currentTimeMillis();
    private final UUID traceId = UUID.randomUUID();
}
