package com.application.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByUserUuidAndFolderUuid(String userUuid, String folderUuid);
    Optional<File> findByUuid(String uuid);

    @Query("SELECT f FROM File f WHERE f.uuid IN :uuids")
    List<File> findAllByUuids(@Param("uuids") List<String> uuids);

    boolean existsByUuid(String uuid);
}
