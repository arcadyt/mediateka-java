package com.acowg.peer.repositories;

import com.acowg.peer.entities.HasMediaOfferingFields;
import com.acowg.peer.entities.MediaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query("SELECT DISTINCT m.catalogId FROM MediaEntity m WHERE m.catalogId IS NOT NULL")
    Set<String> findAllCatalogIdNotNull();

    List<MediaEntity> findByDirectoryId(String directoryId);

    @Modifying
    @Query("DELETE FROM MediaEntity m WHERE m.directory.path = :directoryPath AND m.relativeFilePath NOT IN :relativeFilePaths")
    void deleteByDirectoryPathAndRelativeFilePathsNotIn(
            @Param("directoryPath") String directoryPath,
            @Param("relativeFilePaths") Set<String> relativeFilePaths);

    @Query("""
            SELECT m.catalogId
            FROM MediaEntity m
            WHERE m.directory.path = :directoryPath
            AND m.relativeFilePath NOT IN :relativePaths
            AND m.catalogId IS NOT NULL
            """)
    Set<String> findCatalogIdsByDirectoryPathAndRelativeFilePathsNotIn(
            @Param("directoryPath") String directoryPath,
            @Param("relativePaths") Set<String> relativePaths);

    @Query("SELECT m.relativeFilePath FROM MediaEntity m WHERE m.directory.path = :directoryPath")
    Set<String> findRelativePathsByDirectoryPath(@Param("directoryPath") String directoryPath);
}
