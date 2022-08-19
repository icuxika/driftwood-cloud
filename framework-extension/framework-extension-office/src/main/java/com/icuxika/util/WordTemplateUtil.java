package com.icuxika.util;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 基于 Word 模板生成文档
 */
public class WordTemplateUtil {

    public static Template getTemplate(String directory, String xml) {
        Template template;
        try {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
            configuration.setDirectoryForTemplateLoading(new File(directory));
            template = configuration.getTemplate(xml);
            template.setOutputEncoding(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return template;
    }

    public static File overrideXML(Template template, Map<String, Object> data, String out) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8))) {
            template.process(data, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new File(out);
    }

    public static void overrideDOCX(File xml, String docx, String out) {
        try {
            ZipFile zipFile = new ZipFile(new File(docx));
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(out), StandardCharsets.UTF_8);

            while (entries.hasMoreElements()) {
                ZipEntry next = entries.nextElement();
                InputStream inputStream = zipFile.getInputStream(next);
                zipOutputStream.putNextEntry(new ZipEntry(next.toString()));
                if ("word/document.xml".equals(next.toString())) {
                    InputStream xmlInputStream = new FileInputStream(xml);
                    int docFileLength;
                    while ((docFileLength = xmlInputStream.read()) != -1) {
                        zipOutputStream.write(docFileLength);
                    }
                    xmlInputStream.close();
                } else {
                    int length;
                    while ((length = inputStream.read()) != -1) {
                        zipOutputStream.write(length);
                    }
                    inputStream.close();
                }
            }

            zipOutputStream.close();
            if (!xml.delete()) {
                //
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
