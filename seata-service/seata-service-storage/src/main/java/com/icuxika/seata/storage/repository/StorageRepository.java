package com.icuxika.seata.storage.repository;

import com.icuxika.seata.storage.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, Long> {
}
