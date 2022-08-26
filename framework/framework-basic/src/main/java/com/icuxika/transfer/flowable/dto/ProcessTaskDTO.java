package com.icuxika.transfer.flowable.dto;

import java.util.Map;

/**
 * 任务提交
 */
public class ProcessTaskDTO {

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 流程变量
     */
    private Map<String, Object> variables;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
