package com.icuxika.framework.service.flowable;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FrameworkServiceFlowableApplicationTests {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Test
    void contextLoads() {
    }

    @Test
    void getProcessInstanceIsEnded() {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId("4028bdb5-9fa3-11ec-9840-ca293ae28b28").singleResult();
        Assertions.assertNotNull(historicProcessInstance);
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("4028bdb5-9fa3-11ec-9840-ca293ae28b28").singleResult();
        Assertions.assertNotNull(processInstance);
    }
}
