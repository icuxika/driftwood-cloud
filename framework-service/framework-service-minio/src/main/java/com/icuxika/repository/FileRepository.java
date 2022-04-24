package com.icuxika.repository;

import com.icuxika.modules.file.entity.MinioFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface FileRepository extends JpaRepository<MinioFile, Long> {
    Optional<MinioFile> findByFileSha256(@NonNull String fileSha256);
}
