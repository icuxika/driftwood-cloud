package com.icuxika.framework.basic.transfer.flowable.vo;

import java.util.Date;

public class DoneTaskVO extends CommonTaskVO {

    private Date endTime;

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
