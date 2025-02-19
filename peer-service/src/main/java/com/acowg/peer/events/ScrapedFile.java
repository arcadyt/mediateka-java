package com.acowg.peer.events;

public record ScrapedFile(int sizeInBytes, String relativeFilePath) {
}