package com.icuxika.admin.repository;

import com.icuxika.admin.entity.AdminFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<AdminFile, Long> {
}
