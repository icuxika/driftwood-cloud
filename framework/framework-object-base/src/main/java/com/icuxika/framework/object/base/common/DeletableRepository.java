package com.icuxika.framework.object.base.common;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 逻辑删除
 *
 * @param <T>
 */
@NoRepositoryBean
public interface DeletableRepository<T extends DeletableEntity, ID> extends CrudRepository<T, ID> {

    @Transactional
    @Modifying
    @Query("update #{#entityName} set deleted = true, updateTime = :#{T(java.time.LocalDateTime).now()}, deleteTime = :#{T(java.time.LocalDateTime).now()} where id = :id and deleted = false")
    int logicDeleteById(@Param("id") ID id);

    @Transactional
    @Modifying
    @Query("update #{#entityName} set deleted = true, updateTime = :#{T(java.time.LocalDateTime).now()}, deleteTime = :#{T(java.time.LocalDateTime).now()} where id in :ids and deleted = false")
    int logicDeleteAllById(@Param("ids") Iterable<? extends ID> ids);

}
