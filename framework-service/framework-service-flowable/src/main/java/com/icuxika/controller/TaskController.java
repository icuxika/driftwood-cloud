package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.exception.GlobalServiceException;
import com.icuxika.transfer.flowable.dto.NewTaskDTO;
import com.icuxika.transfer.flowable.dto.ProcessTaskDTO;
import com.icuxika.transfer.flowable.vo.DoneTaskVO;
import com.icuxika.transfer.flowable.vo.NewTaskVO;
import com.icuxika.transfer.flowable.vo.TaskVO;
import com.icuxika.util.SecurityUtil;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("task")
public class TaskController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 创建任务
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @PostMapping("createTask")
    public ApiData<NewTaskVO> createTask(@RequestBody NewTaskDTO newTaskDTO, HttpServletRequest request) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(newTaskDTO.getProcessDefinitionKey()).latestVersion().singleResult();
        if (processDefinition == null) {
            throw new GlobalServiceException("未部署key为[" + newTaskDTO.getProcessDefinitionKey() + "]的流程");
        }
        Authentication.setAuthenticatedUserId(newTaskDTO.getCreateUserId().toString());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(
                newTaskDTO.getProcessDefinitionKey(),
                newTaskDTO.getBusinessKey(),
                newTaskDTO.getVariables(),
                SecurityUtil.getTenantId());
        NewTaskVO newTaskVO = new NewTaskVO();
        BeanUtils.copyProperties(processInstance, newTaskVO);
        return ApiData.ok(newTaskVO);
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
    public ApiData<PageImpl<TaskVO>> getTaskPage(@PageableDefault Pageable pageable, String userId, HttpServletRequest request) {
        TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateOrAssigned(userId).taskTenantId(SecurityUtil.getTenantId());
        long total = taskQuery.count();
        List<Task> taskList = taskQuery.listPage((int) pageable.getOffset(), pageable.getPageSize());
        return ApiData.ok(new PageImpl<>(task2VO(taskList), pageable, total));
    }

    /**
     * 获取用户任务列表
     *
     * @return
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @GetMapping("getTaskList")
    public ApiData<List<TaskVO>> getTaskList(HttpServletRequest request) {
        return ApiData.ok(task2VO(taskService.createTaskQuery().taskTenantId(SecurityUtil.getTenantId()).list()));
    }

    /**
     * 获取已完成用户任务分页
     *
     * @return
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @GetMapping("getDoneTaskPage")
    public ApiData<PageImpl<DoneTaskVO>> getDoneTaskPage(@PageableDefault Pageable pageable, String userId, HttpServletRequest request) {
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).taskTenantId(SecurityUtil.getTenantId()).finished();
        long total = historicTaskInstanceQuery.count();
        List<HistoricTaskInstance> historicTaskInstanceList = historicTaskInstanceQuery.listPage((int) pageable.getOffset(), pageable.getPageSize());
        return ApiData.ok(new PageImpl<>(doneTask2VO(historicTaskInstanceList), pageable, total));
    }

    /**
     * 获取已完成用户任务列表
     *
     * @return
     */
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('ADMIN')")
    @GetMapping("getDoneTaskList")
    public ApiData<List<DoneTaskVO>> getDoneTaskList(HttpServletRequest request) {
        return ApiData.ok(doneTask2VO(historyService.createHistoricTaskInstanceQuery().taskTenantId(SecurityUtil.getTenantId()).finished().list()));
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
