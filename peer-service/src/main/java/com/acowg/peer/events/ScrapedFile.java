package com.acowg.peer.events;

public record ScrapedFile(long sizeInBytes, String relativeFilePath) {
}