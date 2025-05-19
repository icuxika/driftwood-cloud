package com.icuxika.framework.object.base.common;

import com.baomidou.mybatisplus.annotation.TableLogic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass
public class DeletableEntity extends BaseEntity {

    @TableLogic
    @Column(name = "deleted", nullable = false, insertable = false, columnDefinition = "TINYINT(1) default false")
    private Boolean deleted;

    /**
     * MyBatis 方式只能借助自动填充更新`updateTime`作为删除时间，而 Hibernate 方式会同时更新`updateTime`和`deleteTime`
     * <br/>
     * MyBatis`删除接口自动填充功能失效`时要注意应该使用`int deleteById(T entity);`， <a href="https://baomidou.com/pages/6b03c5/#%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98">逻辑删除-常见问题</a>
     */
    @Column()
    private LocalDateTime deleteTime;

}
