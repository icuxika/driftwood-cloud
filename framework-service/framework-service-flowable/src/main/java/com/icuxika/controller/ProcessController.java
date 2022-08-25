package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.transfer.flowable.NewProcessDTO;
import com.icuxika.transfer.flowable.ProcessTaskDTO;
import com.icuxika.transfer.flowable.TaskVO;
import com.icuxika.util.SecurityUtil;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private HistoryService historyService;

    /**
     * 创建流程
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @PostMapping("startProcess")
    public ApiData<String> startProcess(@RequestBody NewProcessDTO newProcessDTO, HttpServletRequest request) {
        Authentication.setAuthenticatedUserId(newProcessDTO.getCreateUserId().toString());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                newProcessDTO.getProcessDefinitionKey(),
                newProcessDTO.getBusinessKey(),
                newProcessDTO.getVariables());
        return ApiData.ok(processInstance.getProcessInstanceId());
    }

    /**
     * 处理任务
     *
     * @return
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @PostMapping("submitTask")
    public ApiData<Object> submitTask(@RequestBody ProcessTaskDTO processTaskDTO, HttpServletRequest request) {
        Task task = taskService.createTaskQuery().taskId(processTaskDTO.getTaskId()).singleResult();
        if (task == null) {
            return ApiData.errorMsg("任务查询失败");
        }
        if (!Long.valueOf(task.getAssignee()).equals(SecurityUtil.getUserId())) {
            return ApiData.errorMsg("无权处理");
        }
        taskService.complete(processTaskDTO.getTaskId(), processTaskDTO.getVariables());
        return ApiData.okMsg("处理成功");
    }

    /**
     * 获取用户任务分页
     *
     * @return
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @GetMapping("getTaskPage")
    public ApiData<List<TaskVO>> getTaskPage(@PageableDefault Pageable pageable, String userId, HttpServletRequest request) {
        List<Task> taskList = taskService.createTaskQuery().taskCandidateOrAssigned(userId).listPage((int) pageable.getOffset(), pageable.getPageSize());
        return ApiData.ok(task2VO(taskList));
    }

    /**
     * 获取用户任务列表
     *
     * @return
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @GetMapping("getTaskList")
    public ApiData<List<TaskVO>> getTaskList(HttpServletRequest request) {
        return ApiData.ok(task2VO(taskService.createTaskQuery().list()));
    }

    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @GetMapping("getHistoricTaskInstance")
    public ApiData<List<HistoricTaskInstance>> getHistoricTaskInstance(HttpServletRequest request) {
        return ApiData.ok(historyService.createHistoricTaskInstanceQuery().list());
    }

    /**
     * 获取已经部署的流程定义列表
     *
     * @return
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @GetMapping("getProcessDefinitionList")
    public ApiData<List<ProcessDefinition>> getProcessDefinitionList(HttpServletRequest request) {
        return ApiData.ok(repositoryService.createProcessDefinitionQuery().list());
    }

    /**
     * 获取流程图
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @GetMapping("getProcessDiagram")
    public void getProcessDiagram(@RequestParam String processDefinitionId, HttpServletRequest request, HttpServletResponse response) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        ProcessEngineConfiguration engineConfiguration = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
        try (
                InputStream inputStream = diagramGenerator.generateDiagram(bpmnModel, "png", Collections.EMPTY_LIST, Collections.EMPTY_LIST, engineConfiguration.getActivityFontName(), engineConfiguration.getLabelFontName(), engineConfiguration.getAnnotationFontName(), engineConfiguration.getClassLoader(), 1.0, true);
                OutputStream outputStream = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
            throw new RuntimeException(e);
        }
    }

    private List<TaskVO> task2VO(List<Task> taskList) {
        return taskList.stream().map(task -> {
            TaskVO taskVO = new TaskVO();
            BeanUtils.copyProperties(task, taskVO);
            return taskVO;
        }).collect(Collectors.toList());
    }
}
