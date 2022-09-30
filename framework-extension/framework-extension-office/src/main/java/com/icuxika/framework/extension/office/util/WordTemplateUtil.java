package com.icuxika.framework.extension.office.util;

import freemarker.cache.ByteArrayTemplateLoader;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 基于 Word 模板生成文档
 * 采用将 Word 文件中的 word/document.xml 抽取出来进行数据填充后再写入 Word文件的方式
 */
public class WordTemplateUtil {

    private static final Logger L = LoggerFactory.getLogger(WordTemplateUtil.class);

    /**
     * 从 src/main/resources 目录下获取模板文件
     *
     * @param directory 加载目录
     * @param xml       word/document.xml
     * @return 模板数据
     */
    public static Template getTemplateFromResource(String directory, String xml) {
        Template template;
        try {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
            configuration.setTemplateLoader(new ClassTemplateLoader(WordTemplateUtil.class, directory));
            template = configuration.getTemplate(xml);
            template.setOutputEncoding(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return template;
    }

    /**
     * 从文件流中获取模板
     *
     * @param inputStream 输入流
     * @return 模板数据
     */
    public static Template getTemplateFromInputStream(InputStream inputStream) {
        Template template;
        ByteArrayTemplateLoader byteArrayTemplateLoader = new ByteArrayTemplateLoader();
        String tempName = "document" + getLocalDateTimeFormat() + ".xml";
        try {
            byteArrayTemplateLoader.putTemplate(tempName, inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
            configuration.setTemplateLoader(byteArrayTemplateLoader);
            template = configuration.getTemplate(tempName);
            template.setOutputEncoding(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return template;
    }

    /**
     * 将数据写入到 word/document.xml
     *
     * @param template word/document.xml 对应的模板数据
     * @param data     数据
     * @return 经过数据覆盖的 临时 word/document.xml
     */
    public static File overrideXML(Template template, Map<String, Object> data) {
        File tempFile;
        try {
            tempFile = File.createTempFile("driftwood-" + getLocalDateTimeFormat(), ".xml");
            if (L.isInfoEnabled()) {
                L.info("填充数据Word xml文件：" + tempFile.getAbsoluteFile());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
            template.process(data, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tempFile;
    }

    /**
     * 将经过数据覆盖的 word/document.xml 写入到目标输出 Word 文件中
     *
     * @param xml  经过数据覆盖的 word/document.xml
     * @param docx 模板 Word 文件
     * @param out  目标输出文件
     */
    public static void overrideDOCX(File xml, String docx, OutputStream out) {
        try {
            try (
                    ZipFile zipFile = new ZipFile(new File(docx));
                    ZipOutputStream zipOutputStream = new ZipOutputStream(out);
            ) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry next = entries.nextElement();
                    zipOutputStream.putNextEntry(new ZipEntry(next.toString()));
                    if ("word/document.xml".equals(next.toString())) {
                        try (InputStream xmlInputStream = new FileInputStream(xml)) {
                            int docFileLength;
                            while ((docFileLength = xmlInputStream.read()) != -1) {
                                zipOutputStream.write(docFileLength);
                            }
                        }
                    } else {
                        try (InputStream inputStream = zipFile.getInputStream(next)) {
                            int aByte;
                            while ((aByte = inputStream.read()) != -1) {
                                zipOutputStream.write(aByte);
                            }
                        }
                    }
                }
            }
            if (!xml.delete()) {
                if (L.isWarnEnabled()) {
                    L.warn("临时文件删除失败");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将经过数据覆盖的 word/document.xml 写入到目标输出 Word 文件中
     *
     * @param xml  经过数据覆盖的 word/document.xml
     * @param docx 模板 Word 文件
     * @param out  目标输出文件
     */
    public static void overrideDOCX(File xml, InputStream docx, OutputStream out) {
        try (
                ZipInputStream zipInputStream = new ZipInputStream(docx);
                ZipOutputStream zipOutputStream = new ZipOutputStream(out)
        ) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                zipOutputStream.putNextEntry(new ZipEntry(zipEntry.toString()));
                if ("word/document.xml".equals(zipEntry.getName())) {
                    try (InputStream xmlInputStream = new FileInputStream(xml)) {
                        int docFileLength;
                        while ((docFileLength = xmlInputStream.read()) != -1) {
                            zipOutputStream.write(docFileLength);
                        }
                    }
                } else {
                    int aByte;
                    while ((aByte = zipInputStream.read()) != -1) {
                        zipOutputStream.write(aByte);
                    }
                }
            }
            if (!xml.delete()) {
                if (L.isWarnEnabled()) {
                    L.warn("临时文件删除失败");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 模板文件由外部流传入，填充数据到 Word 并导出
     *
     * @param data         数据
     * @param xml          Word 模板文件解压得到的 word/document.xml
     * @param docx         Word 模板文件
     * @param outputStream 输出目标
     */
    public static void processDocx(Map<String, Object> data, InputStream xml, InputStream docx, OutputStream outputStream) {
        Template template = WordTemplateUtil.getTemplateFromInputStream(xml);
        File temp = overrideXML(template, data);
        overrideDOCX(temp, docx, outputStream);
    }

    /**
     * 模板文件位于 src/main/resources 目录下填充数据到 Word 并导出
     *
     * @param data         数据
     * @param xml          Word 模板文件解压得到的 word/document.xml
     * @param docx         Word 模板文件
     * @param outputStream 输出目标
     */
    public static void processDocx(Map<String, Object> data, String directory, String xml, String docx, OutputStream outputStream) {
        Template template = WordTemplateUtil.getTemplateFromResource(directory, xml);
        File temp = overrideXML(template, data);
        overrideDOCX(temp, WordTemplateUtil.class.getResourceAsStream(directory + "/" + docx), outputStream);
    }

    private static String getLocalDateTimeFormat() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
