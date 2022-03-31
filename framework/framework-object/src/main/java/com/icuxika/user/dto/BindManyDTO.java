package com.icuxika.user.dto;

import java.util.List;

/**
 * 创建关联关系的DTO n -> n
 */
public class BindManyDTO {

    private List<Long> idList;

    private List<Long> relatedIdList;

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public List<Long> getRelatedIdList() {
        return relatedIdList;
    }

    public void setRelatedIdList(List<Long> relatedIdList) {
        this.relatedIdList = relatedIdList;
    }
}
