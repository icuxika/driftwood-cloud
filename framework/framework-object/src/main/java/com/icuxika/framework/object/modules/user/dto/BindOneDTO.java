package com.icuxika.framework.object.modules.user.dto;

import java.util.List;

/**
 * 创建关联关系的DTO 1 -> n
 */
public class BindOneDTO {

    private Long id;

    private List<Long> idList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

}

