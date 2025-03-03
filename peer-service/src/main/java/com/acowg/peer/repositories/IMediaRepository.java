package com.acowg.peer.repositories;

import com.acowg.peer.entities.HasMediaOfferingFields;
import com.acowg.peer.entities.MediaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface IMediaRepository extends JpaRepository<MediaEntity, String> {
    @Modifying
    void deleteByCatalogId(String catalogId);

    @Modifying
    void deleteByCatalogIdIn(Set<String> catalogIds);

    Optional<MediaEntity> findByCatalogId(String catalogId);

    Set<MediaEntity> findByCatalogIdIn(Set<String> catalogIds);

    Page<HasMediaOfferingFields> findByCatalogIdIsNull(Pageable pageable);

    @Query("""
            SELECT DISTINCT m.catalogId
            FROM MediaEntity m
            WHERE m.catalogId IS NOT NULL
            """)
    Set<String> findAllCatalogIdNotNull();

    @Modifying
    @Query("""
            DELETE FROM MediaEntity m
            WHERE m.baseDirectory.path = :baseDirectoryPath
            AND m.relativeFilePath NOT IN :relativeFilePaths
            """)
    void deleteByBaseDirectoryPathAndRelativeFilePathsNotIn(String baseDirectoryPath, Set<String> relativeFilePaths);

    @Query("""
            SELECT m.catalogId
            FROM MediaEntity m
            WHERE m.directory.path = :directoryPath
            AND m.relativeFilePath NOT IN :relativePaths
            AND m.catalogId IS NOT NULL
            """)
    Set<String> findCatalogIdsByDirectoryPathAndRelativeFilePathsNotIn(
            String directoryPath, Set<String> relativePaths);

    @Query("""
            SELECT m.relativeFilePath
            FROM MediaEntity m
            WHERE m.directory.path = :directoryPath
            """)
    Set<String> findRelativeFilePathsByDirectoryPath(String directoryPath);
}
