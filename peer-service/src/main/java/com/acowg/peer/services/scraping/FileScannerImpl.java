package com.acowg.peer.services.scraping;

import com.acowg.peer.config.MediaTypeConfig;
import com.acowg.peer.events.ScrapedFile;
import com.acowg.peer.services.locks.Drive;
import com.acowg.peer.services.locks.RequiresDriveLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.io.MoreFiles.getFileExtension;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileScannerImpl implements IFileScanner {

    private final MediaTypeConfig mediaTypeConfig;

    @Override
    @RequiresDriveLock
    public Set<ScrapedFile> scanDirectory(@Drive Path root) {
        try (Stream<Path> stream = Files.walk(root)) {
            return stream.filter(Files::isRegularFile)
                    .filter(this::isSupportedMediaFile)
                    .map(this::toScrapedFile)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Error scanning directory: {}", root, e);
            return Set.of();
        }
    }

    private boolean isSupportedMediaFile(Path path) {
        String extension = getFileExtension(path);
        return mediaTypeConfig.getExtensionToMediaType().containsKey(extension);
    }

    private ScrapedFile toScrapedFile(Path file) {
        try {
            int sizeInBytes = (int) Files.size(file);
            return new ScrapedFile(sizeInBytes, file.toString());
        } catch (IOException e) {
            log.error("Failed to retrieve file size for {}", file, e);
            return null;
        }
    }
}
