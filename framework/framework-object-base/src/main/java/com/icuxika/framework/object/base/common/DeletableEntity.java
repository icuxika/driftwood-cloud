package com.icuxika.framework.object.base.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public class DeletableEntity extends BaseEntity {

    @Column(name = "deleted", nullable = false, insertable = false, columnDefinition = "bigint default 0")
    private Long deleted;

    @Column()
    private LocalDateTime deleteTime;

    public Long getDeleted() {
        return deleted;
    }

    public void setDeleted(Long deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime;
    }
}
