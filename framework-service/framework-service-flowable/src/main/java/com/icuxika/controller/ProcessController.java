package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.exception.GlobalServiceException;
import com.icuxika.transfer.flowable.ProcessDefinitionVO;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("process")
public class ProcessController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private HistoryService historyService;

    /**
     * 获取已经部署的流程定义分页
     *
     * @return
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @GetMapping("getProcessDefinitionPage")
    public ApiData<PageImpl<ProcessDefinitionVO>> getProcessDefinitionPage(@PageableDefault Pageable pageable, HttpServletRequest request) {
        long total = repositoryService.createProcessDefinitionQuery().latestVersion().count();
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().latestVersion().listPage((int) pageable.getOffset(), pageable.getPageSize());
        return ApiData.ok(new PageImpl<>(processDefinition2VO(processDefinitionList), pageable, total));
    }

    /**
     * 获取已经部署的流程定义列表
     *
     * @return
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @GetMapping("getProcessDefinitionList")
    public ApiData<List<ProcessDefinitionVO>> getProcessDefinitionList(HttpServletRequest request) {
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().latestVersion().list();
        return ApiData.ok(processDefinition2VO(processDefinitionList));
    }

    /**
     * 获取流程图
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @GetMapping("getProcessDiagram")
    public void getProcessDiagram(@RequestParam String processDefinitionId, HttpServletRequest request, HttpServletResponse response) {
        writeProcessDiagram(processDefinitionId, Collections.EMPTY_LIST, Collections.EMPTY_LIST, response);
    }

    /**
     * 获取任务trace流程图
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @GetMapping("getProcessDiagramTrace")
    public void getProcessDiagramTrace(@RequestParam String processInstanceId, HttpServletRequest request, HttpServletResponse response) {
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

    /**
     * 删除已经部署的流程
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @DeleteMapping("deleteByDeploymentId/{deploymentId}")
    public void deleteByDeploymentId(@PathVariable("deploymentId") String deploymentId, HttpServletRequest request) {
        // 级联删除流程实例，历史流程等数据
        repositoryService.deleteDeployment(deploymentId, true);
    }

    /**
     * 删除流程实例
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @PostMapping("deleteProcessInstance")
    public void deleteProcessInstance(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("running") Boolean running, HttpServletRequest request) {
        if (running) {
            runtimeService.deleteProcessInstance(processInstanceId, "运行时任务清理");
        } else {
            historyService.deleteHistoricProcessInstance(processInstanceId);
        }
    }

    /**
     * 删除所有流程实例
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @PostMapping("deleteAllProcessInstance")
    public void deleteAllProcessInstance(HttpServletRequest request) {
        runtimeService.createProcessInstanceQuery().list().forEach(processInstance -> runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(), "运行时任务清理"));
        historyService.createHistoricProcessInstanceQuery().list().forEach(historicProcessInstance -> historyService.deleteHistoricProcessInstance(historicProcessInstance.getId()));
    }

    private List<ProcessDefinitionVO> processDefinition2VO(List<ProcessDefinition> processDefinitionList) {
        return processDefinitionList.stream().map(processDefinition -> {
            ProcessDefinitionVO processDefinitionVO = new ProcessDefinitionVO();
            BeanUtils.copyProperties(processDefinition, processDefinitionVO);
            return processDefinitionVO;
        }).collect(Collectors.toList());
    }

    public void writeProcessDiagram(String processDefinitionId, List<String> highLightedActivities, List<String> highLightedFlows, HttpServletResponse response) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        ProcessEngineConfiguration engineConfiguration = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engineConfiguration.getProcessDiagramGenerator();

        try (
                InputStream inputStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivities, highLightedFlows, engineConfiguration.getActivityFontName(), engineConfiguration.getLabelFontName(), engineConfiguration.getAnnotationFontName(), engineConfiguration.getClassLoader(), 1.0, true);
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
