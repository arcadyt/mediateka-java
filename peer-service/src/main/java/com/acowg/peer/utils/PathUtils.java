package com.acowg.peer.utils;

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
          Path root = path.toAbsolutePath().getRoot();
          if (root == null) {
               throw new IllegalArgumentException("Path does not have a root: " + path);
          }
          return root.toString().toUpperCase().replace("\\", "/");
     }
}
