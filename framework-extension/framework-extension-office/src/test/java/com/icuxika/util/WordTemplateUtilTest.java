package com.icuxika.util;

import com.icuxika.framework.extension.office.util.WordTemplateUtil;
import freemarker.template.Template;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class WordTemplateUtilTest {

    public static void main(String[] args) {
        Template template = WordTemplateUtil.getTemplate("C:\\Users\\icuxika\\IdeaProjects\\driftwood-cloud\\framework-extension\\framework-extension-office\\src\\main\\resources\\template", "测试模板.xml");
        Map<String, Object> map = new HashMap<>();
        map.put("name", "名称");
        map.put("desc", "描述");
        File xml = WordTemplateUtil.overrideXML(template, map, "C:\\Users\\icuxika\\Desktop\\out.xml");
        WordTemplateUtil.overrideDOCX(xml, "C:\\Users\\icuxika\\IdeaProjects\\driftwood-cloud\\framework-extension\\framework-extension-office\\src\\main\\resources\\template\\测试模板.docx", "C:\\Users\\icuxika\\Desktop\\out.docx");
    }
}