package com.icuxika.common;

import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Where(clause = "deleted = 0")
public class DeletableEntity extends BaseEntity {

    @Column(name = "deleted", nullable = false, insertable = false, columnDefinition = "int default 0")
    private Integer deleted;

    @Column()
    private LocalDateTime deleteTime;

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime;
    }
}
