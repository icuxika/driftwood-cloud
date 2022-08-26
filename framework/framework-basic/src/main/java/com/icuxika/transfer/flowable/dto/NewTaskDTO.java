package com.icuxika.transfer.flowable.dto;

import java.util.Map;

/**
 * 创建新流程
 */
public class NewTaskDTO {

    /**
     * 流程创建用户
     */
    private Long createUserId;

    /**
     * 流程定义key
     */
    private String processDefinitionKey;

    /**
     * 流程绑定的业务key
     */
    private String businessKey;

    /**
     * 流程变量
     */
    private Map<String, Object> variables;

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
