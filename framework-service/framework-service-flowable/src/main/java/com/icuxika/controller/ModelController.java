package com.icuxika.controller;

import com.icuxika.common.ApiData;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("model")
public class ModelController {

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ModelService modelService;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 获取模型列表
     *
     * @return
     */
    @GetMapping("getModelList")
    public ApiData<List<Model>> getModelList() {
        List<Model> modelList = modelRepository.findByModelType(AbstractModel.MODEL_TYPE_BPMN, "");
        return ApiData.ok(modelList);
    }

    /**
     * 部署模型
     *
     * @param id
     * @return
     */
    @GetMapping("deployModel")
    public ApiData<Object> deployModel(String id) {
        Model model;
        try {
            model = modelService.getModel(id);
        } catch (Exception e) {
            return ApiData.errorMsg("模型不存在");
        }
        BpmnModel bpmnModel = modelService.getBpmnModel(model);
        Deployment deployment = repositoryService.createDeployment()
                .name(model.getName())
                .addBpmnModel(model.getKey() + ".bpmn20.xml", bpmnModel)
                .deploy();
        return ApiData.okMsg("部署成功");
    }
}
