package com.icuxika.framework.service.activiti.controller;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.exception.GlobalServiceException;
import com.icuxika.framework.basic.transfer.flowable.vo.ProcessDefinitionVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * bpmn-js
 * LogicFlow
 */
@RestController
@RequestMapping("process")
@RequiredArgsConstructor
public class ProcessController {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;

    @GetMapping("getProcessDefinitionPage")
    public ApiData<PageImpl<ProcessDefinitionVO>> getProcessDefinitionPage(@PageableDefault Pageable pageable) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().latestVersion();
        long total = processDefinitionQuery.count();
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage((int) pageable.getOffset(), pageable.getPageSize());
        return ApiData.ok(new PageImpl<>(processDefinition2VO(processDefinitionList), pageable, total));
    }

    /**
     * 根据流程图原始 bpmn20.xml 设置的 id 获取流程图输出流
     *
     * @param processDefinitionKey 如 test
     */
    @GetMapping("getProcessDiagramByProcessDefinitionKey")
    public void getProcessDiagram(@RequestParam String processDefinitionKey, HttpServletResponse response) {
        writeProcessDiagramByProcessDefinitionKey(processDefinitionKey, Collections.EMPTY_LIST, Collections.EMPTY_LIST, response);
    }

    /**
     * 根据部署后的流程图的id获取流程图输出流
     *
     * @param processDefinitionId 如 test:2:824884a8-ae28-11ef-bd1b-00155d325800
     */
    @GetMapping("getProcessDiagramByProcessDefinitionId")
    public void getProcessDiagramByProcessDefinitionId(@RequestParam String processDefinitionId, HttpServletResponse response) {
        writeProcessDiagramByProcessDefinitionId(processDefinitionId, Collections.EMPTY_LIST, Collections.EMPTY_LIST, response);
    }

    @GetMapping("getProcessDiagramTrace")
    public void getProcessDiagramTrace(@RequestParam String processInstanceId, HttpServletResponse response) {
        String processDefinitionId;
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance != null) {
            // 流程正在运行
            processDefinitionId = processInstance.getProcessDefinitionId();
        } else {
            // 流程已经结束
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        }

        List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        List<String> highLightedActivities = new ArrayList<>();
        List<String> highLightedFlows = new ArrayList<>();
        historicActivityInstanceList.forEach(historicActivityInstance -> {
            String activityId = historicActivityInstance.getActivityId();
            highLightedActivities.add(activityId);
            if ("sequenceFlow".equals(historicActivityInstance.getActivityType())) {
                highLightedFlows.add(activityId);
            }
        });

        writeProcessDiagram(processDefinitionId, highLightedActivities, highLightedFlows, response);
    }

    private List<ProcessDefinitionVO> processDefinition2VO(List<ProcessDefinition> processDefinitionList) {
        return processDefinitionList.stream().map(processDefinition -> {
            ProcessDefinitionVO processDefinitionVO = new ProcessDefinitionVO();
            BeanUtils.copyProperties(processDefinition, processDefinitionVO);
            return processDefinitionVO;
        }).collect(Collectors.toList());
    }

    private void writeProcessDiagramByProcessDefinitionKey(String processDefinitionKey, List<String> highLightedActivities, List<String> highLightedFlows, HttpServletResponse response) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
        writeProcessDiagram(processDefinition.getId(), highLightedActivities, highLightedFlows, response);
    }

    private void writeProcessDiagramByProcessDefinitionId(String processDefinitionId, List<String> highLightedActivities, List<String> highLightedFlows, HttpServletResponse response) {
        writeProcessDiagram(processDefinitionId, highLightedActivities, highLightedFlows, response);
    }

    private void writeProcessDiagram(String processDefinitionId, List<String> highLightedActivities, List<String> highLightedFlows, HttpServletResponse response) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
        try (
                InputStream inputStream = diagramGenerator.generateDiagram(bpmnModel, highLightedActivities, highLightedFlows, "宋体", "宋体", "宋体");
                OutputStream outputStream = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new GlobalServiceException("流程图生成失败：" + e.getMessage());
        }
    }
}
