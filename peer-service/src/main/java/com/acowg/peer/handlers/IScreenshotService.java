package com.acowg.peer.handlers;

import com.acowg.peer.services.locks.Drive;
import com.acowg.peer.services.locks.RequiresDriveLock;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface IScreenshotService {
    @RequiresDriveLock
    List<byte[]> captureScreenshots(@Drive Path videoPath, int quantity) throws IOException;

    @RequiresDriveLock
    byte[] captureFrame(@Drive Path videoPath, int frameNumber) throws IOException;
}
