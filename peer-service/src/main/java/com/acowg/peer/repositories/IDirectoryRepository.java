package com.acowg.peer.repositories;

import com.acowg.peer.entities.DirectoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RepositoryRestResource(path = "directories")
public interface IDirectoryRepository extends JpaRepository<DirectoryEntity, String> {
    Optional<DirectoryEntity> findByPath(String path);
}
