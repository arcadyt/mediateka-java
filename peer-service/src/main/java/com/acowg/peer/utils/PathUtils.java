package com.acowg.peer.utils;

import com.google.common.base.Strings;

import java.nio.file.Path;

/**
 * Utility class for path-related operations.
 */
public class PathUtils {

    /**
     * Resolves the root identifier (drive letter on Windows, root or mount point on Linux).
     *
     * @param path The file path as a Path object.
     * @return The root identifier in uppercase.
     * @throws IllegalArgumentException if the path is invalid or does not have a root.
     */
    public static String extractRootIdentifier(Path path) {
        Path absolutePath = path.toAbsolutePath();
        Path root = absolutePath.getRoot();

        if (root == null) {
            throw new IllegalArgumentException("Path does not have a root: " + path);
        }

        String rootStr = root.toString().replace("\\", "/").toUpperCase(); // Normalize Windows-style paths

        // Handle Windows (Drive letter: C:/, D:/, etc.)
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return rootStr;
        }

        // Handle Linux (Check for mount points in /mnt, /media, etc.)
        String pathStr = absolutePath.toString();
        if (pathStr.startsWith("/mnt/") || pathStr.startsWith("/media/")) {
            String[] parts = pathStr.split("/");
            if (parts.length >= 3) { // Extract "/mnt/drive_name" or "/media/drive_name"
                return ("/" + parts[1] + "/" + parts[2]).toUpperCase();
            }
        }

        // Default: Return root `/` for Linux if no mount point is detected
        return rootStr;
    }

    /**
     * Extracts file extension from relative file path.
     *
     * @param filePath Path of the file
     * @return Lowercase file extension or null
     */
    public static String extractExtension(String filePath) {
        if (Strings.isNullOrEmpty(filePath)) {
            return null;
        }

        int lastDotIndex = filePath.lastIndexOf('.');
        return (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1)
                ? filePath.substring(lastDotIndex + 1).toLowerCase()
                : null;
    }
}
