package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.transfer.flowable.NewProcessDTO;
import com.icuxika.transfer.flowable.ProcessTaskDTO;
import com.icuxika.transfer.flowable.TaskVO;
import com.icuxika.util.SecurityUtil;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('USER')")
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
    @PreAuthorize("@fvs.isFeign(#request) || hasRole('USER')")
    @GetMapping("getTaskList")
    public ApiData<List<TaskVO>> getTaskList(HttpServletRequest request) {
        return ApiData.ok(task2VO(taskService.createTaskQuery().list()));
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

    private List<TaskVO> task2VO(List<Task> taskList) {
        return taskList.stream().map(task -> {
            TaskVO taskVO = new TaskVO();
            BeanUtils.copyProperties(task, taskVO);
            return taskVO;
        }).collect(Collectors.toList());
    }
}
