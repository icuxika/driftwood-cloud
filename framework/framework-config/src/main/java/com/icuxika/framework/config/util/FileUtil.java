package com.icuxika.framework.config.util;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class FileUtil {

    /**
     * 对 HttpServletResponse 写出文件
     *
     * @param response HttpServletResponse
     * @param filename 文件名
     * @param consumer 交由调用方写入文件数据到 OutputStream
     * @throws IOException
     */
    public static void responseFile(HttpServletResponse response, String filename, Consumer<OutputStream> consumer) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        // Content-Disposition: attachment; filename="filename.jpg"
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build());
        headers.toSingleValueMap().forEach(response::addHeader);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // Content-Type: application/octet-stream
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        consumer.accept(response.getOutputStream());
    }
}
