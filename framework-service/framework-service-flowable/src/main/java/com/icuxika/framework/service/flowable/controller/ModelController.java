package com.icuxika.framework.service.flowable.controller;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.exception.GlobalServiceException;
import com.icuxika.framework.basic.transfer.flowable.vo.ModelVO;
import com.icuxika.framework.security.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.ui.common.util.XmlUtil;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.service.FlowableModelQueryService;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;


@RestController
@RequestMapping("model")
public class ModelController {

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ModelService modelService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    protected FlowableModelQueryService modelQueryService;

    protected BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();

    /**
     * 获取模型分页
     *
     * @return
     */
    @GetMapping("getModelPage")
    public ApiData<PageImpl<ModelVO>> getModelPage(@PageableDefault Pageable pageable) {
        List<Model> modelList = modelRepository.findByModelType(AbstractModel.MODEL_TYPE_BPMN, "");
        return ApiData.ok(new PageImpl<>(model2VO(modelList), pageable, modelList.size()));
    }

    /**
     * 获取模型列表
     *
     * @return
     */
    @GetMapping("getModelList")
    public ApiData<List<ModelVO>> getModelList() {
        // Flowable 会将模型存储到 ACT_DE_MODEL，因此 repositoryService.createModelQuery().list() 无法获取在 Flowable Modeler 新建的模型
        List<Model> modelList = modelRepository.findByModelType(AbstractModel.MODEL_TYPE_BPMN, "");
        return ApiData.ok(model2VO(modelList));
    }

    /**
     * 删除模型
     *
     * @return
     */
    @DeleteMapping("{modelId}")
    public ApiData<Void> deleteById(@PathVariable("modelId") String modelId) {
        Model model = null;
        try {
            model = modelService.getModel(modelId);
        } catch (Exception e) {
            // do nothing
        }
        if (model != null) {
            try {
                modelService.deleteModel(modelId);
            } catch (Exception e) {
                throw new GlobalServiceException("模型删除失败：" + e.getMessage());
            }
        }
        return ApiData.okMsg("删除成功");
    }

    /**
     * 通过 model id 部署模型
     *
     * @param id
     * @return
     */
    @PostMapping("deployModelByModelId")
    public ApiData<String> deployModelByModelId(String id) {
        Model model;
        try {
            model = modelService.getModel(id);
        } catch (Exception e) {
            return ApiData.errorMsg("模型不存在");
        }
        BpmnModel bpmnModel = modelService.getBpmnModel(model);
        Deployment deployment = repositoryService.createDeployment()
                .name(model.getName())
                .tenantId(SecurityUtil.getTenantId())
                .addBpmnModel(model.getKey() + ".bpmn20.xml", bpmnModel)
                .deploy();
        return ApiData.okMsg(deployment.getId());
    }

    /**
     * 通过 xml 部署模型
     *
     * @return
     */
    @PostMapping("deployModelByXML")
    public ApiData<String> deployModelByXML(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        // 判断key是否已经存在
        String fileName = file.getOriginalFilename();
        if (fileName != null && (fileName.endsWith(".bpmn") || fileName.endsWith(".bpmn20.xml"))) {
            try {
                XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
                InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
                BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
                String key = bpmnModel.getMainProcess().getId();
                if (!modelRepository.findByKeyAndType(key, AbstractModel.MODEL_TYPE_BPMN).isEmpty()) {
                    throw new GlobalServiceException("key为[" + key + "]的流程图已经存在");
                }
            } catch (Exception e) {
                String errorMsgPrefix = "部署失败，xml文件校验失败：";
                if (e instanceof GlobalServiceException) {
                    errorMsgPrefix = "部署失败：";
                }
                throw new GlobalServiceException(errorMsgPrefix + e.getMessage());
            }
        }

        // TODO 等待UI适配 Spring Boot 3
        // 调用 Flowable 独立的方式存储模型到 ACT_DE_MODEL，此处与 Flowable Modeler 新建与更新模型都不会存储 tenant id
//        ModelRepresentation modelRepresentation = modelQueryService.importProcessModel(request, file);
        // 从部署开始的操作应根据项目是否需要多租户功能而考虑设置存储 tenant id 信息
//        return deployModelByModelId(modelRepresentation.getId());
        return null;
    }

    /**
     * 通过 zip 部署模型
     * <p>
     * 仅部署，即生成了新流程定义，但是未保存模型
     *
     * @return
     */
    @PostMapping("deployModelByZip")
    public ApiData<String> deployModelByZip(@RequestPart("file") MultipartFile file, @RequestParam("name") String name, HttpServletRequest request) {
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
            deploymentBuilder.name(name);
            deploymentBuilder.addZipInputStream(zipInputStream);
            Deployment deployment = deploymentBuilder.deploy();
            return ApiData.ok(deployment.getId());
        } catch (IOException e) {
            throw new GlobalServiceException("通过Zip部署流程失败：" + e.getMessage());
        }
    }

    private List<ModelVO> model2VO(List<Model> modelList) {
        return modelList.stream().map(model -> {
            ModelVO modelVO = new ModelVO();
            BeanUtils.copyProperties(model, modelVO);
            return modelVO;
        }).collect(Collectors.toList());
    }
}
