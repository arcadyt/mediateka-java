package com.acowg.peer.services.scraping;

import com.acowg.peer.events.ScrapedFile;

import java.nio.file.Path;
import java.util.Set;

public interface IFileScanner {
    Set<ScrapedFile> scanDirectory(Path root);
}
