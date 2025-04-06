package com.application.folder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByUserUuidAndParentFolderIsNull(String userUuid);

    List<Folder> findByUserUuidAndParentFolderUuid(String userUuid, String parentFolderUuid);

    Optional<Folder> findByUuid(String uuid);

    @Query("SELECT f FROM Folder f WHERE f.uuid IN :uuids")
    List<Folder> findAllByUuids(@Param("uuids") List<String> uuids);

    boolean existsByUuid(String uuid);

}


