package com.icuxika.framework.object.base.common;

import com.baomidou.mybatisplus.annotation.TableLogic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public class DeletableEntity extends BaseEntity {

    @TableLogic
    @Column(name = "deleted", nullable = false, insertable = false, columnDefinition = "bigint default 0")
    private Long deleted;

    /**
     * MyBatis 方式只能借助自动填充更新`updateTime`作为删除时间，而 Hibernate 方式会同时更新`updateTime`和`deleteTime`
     * <br/>
     * MyBatis`删除接口自动填充功能失效`时要注意应该使用`int deleteById(T entity);`， <a href="https://baomidou.com/pages/6b03c5/#%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98">逻辑删除-常见问题</a>
     */
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
