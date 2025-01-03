package com.icuxika.framework.service.activiti.controller;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.transfer.flowable.dto.NewTaskDTO;
import com.icuxika.framework.basic.transfer.flowable.dto.ProcessTaskDTO;
import com.icuxika.framework.basic.transfer.flowable.vo.DoneTaskVO;
import com.icuxika.framework.basic.transfer.flowable.vo.NewTaskVO;
import com.icuxika.framework.basic.transfer.flowable.vo.TaskVO;
import lombok.RequiredArgsConstructor;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("task")
@RequiredArgsConstructor
public class TaskController {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    @PostMapping("createTask")
    public ApiData<NewTaskVO> createTask(@RequestBody NewTaskDTO newTaskDTO) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(newTaskDTO.getProcessDefinitionKey()).latestVersion().singleResult();
        if (processDefinition == null) {
            return ApiData.errorMsg("未部署流程图[" + newTaskDTO.getProcessDefinitionKey() + "]");
        }
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                newTaskDTO.getProcessDefinitionKey(),
                newTaskDTO.getBusinessKey(),
                newTaskDTO.getVariables()
        );
        NewTaskVO newTaskVO = new NewTaskVO();
        BeanUtils.copyProperties(processInstance, newTaskVO);
        return ApiData.ok(newTaskVO);
    }

    @GetMapping("getTaskPage")
    public ApiData<PageImpl<TaskVO>> getTaskPage(@PageableDefault Pageable pageable, String userId) {
        TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateOrAssigned(userId);
        long total = taskQuery.count();
        List<Task> taskList = taskQuery.listPage((int) pageable.getOffset(), pageable.getPageSize());
        return ApiData.ok(new PageImpl<>(task2VO(taskList), pageable, total));
    }

    @GetMapping("getDoneTaskPage")
    public ApiData<PageImpl<DoneTaskVO>> getDoneTaskPage(@PageableDefault Pageable pageable, String userId) {
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery().taskCandidateUser(userId).finished();
        long total = historicTaskInstanceQuery.count();
        List<HistoricTaskInstance> historicTaskInstanceList = historicTaskInstanceQuery.listPage((int) pageable.getOffset(), pageable.getPageSize());
        return ApiData.ok(new PageImpl<>(doneTask2VO(historicTaskInstanceList), pageable, total));
    }

    @PostMapping("submitTask")
    public ApiData<Object> submitTask(@RequestBody ProcessTaskDTO processTaskDTO) {
        Task task = taskService.createTaskQuery().taskId(processTaskDTO.getTaskId()).singleResult();
        if (task == null) {
            return ApiData.errorMsg("任务查询失败");
        }
        taskService.complete(processTaskDTO.getTaskId(), processTaskDTO.getVariables());
        return ApiData.okMsg("处理成功");
    }

    private List<TaskVO> task2VO(List<Task> taskList) {
        return taskList.stream().map(task -> {
            TaskVO taskVO = new TaskVO();
            BeanUtils.copyProperties(task, taskVO);
            return taskVO;
        }).collect(Collectors.toList());
    }

    private List<DoneTaskVO> doneTask2VO(List<HistoricTaskInstance> historicTaskInstanceList) {
        return historicTaskInstanceList.stream().map(historicTaskInstance -> {
            DoneTaskVO doneTaskVO = new DoneTaskVO();
            BeanUtils.copyProperties(historicTaskInstance, doneTaskVO);
            return doneTaskVO;
        }).collect(Collectors.toList());
    }
}
