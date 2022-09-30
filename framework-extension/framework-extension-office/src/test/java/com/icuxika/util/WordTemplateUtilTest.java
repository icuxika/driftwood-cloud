package com.icuxika.util;

import com.icuxika.framework.extension.office.util.WordTemplateUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class WordTemplateUtilTest {

    public static void main(String[] args) {
        overrideByResource();
    }

    public static void overrideByStream() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "名称");
        data.put("desc", "描述");
        try (
                FileInputStream xml = new FileInputStream("C:\\Users\\icuxika\\IdeaProjects\\driftwood-cloud\\framework-extension\\framework-extension-office\\src\\main\\resources\\template\\测试模板.xml");
                FileInputStream docx = new FileInputStream("C:\\Users\\icuxika\\IdeaProjects\\driftwood-cloud\\framework-extension\\framework-extension-office\\src\\main\\resources\\template\\测试模板.docx");
                FileOutputStream out = new FileOutputStream("C:\\Users\\icuxika\\Desktop\\out.docx");
        ) {
            WordTemplateUtil.processDocx(data, xml, docx, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void overrideByResource() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "名称");
        data.put("desc", "描述");
        try {
            WordTemplateUtil.processDocx(
                    data,
                    "/template",
                    "测试模板.xml",
                    "测试模板.docx",
                    new FileOutputStream("C:\\Users\\icuxika\\Desktop\\out.docx")
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}