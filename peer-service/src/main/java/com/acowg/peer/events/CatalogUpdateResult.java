package com.acowg.peer.events;

import java.util.Set;

public record CatalogUpdateResult(Set<String> deletedCatalogIds){}
