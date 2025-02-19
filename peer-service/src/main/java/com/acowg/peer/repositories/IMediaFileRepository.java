package com.acowg.peer.repositories;


import com.acowg.peer.entities.MediaFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IMediaFileRepository extends JpaRepository<MediaFileEntity, String> {
    @Modifying
    void deleteByCatalogId(String catalogId);

    @Modifying
    void deleteByCatalogIdIn(Set<String> catalogIds);

    Optional<MediaFileEntity> findByCatalogId(String catalogId);

    List<MediaFileEntity> findByCatalogIdIsNull();

    List<MediaFileEntity> findByCatalogIdIsNotNull();
}
