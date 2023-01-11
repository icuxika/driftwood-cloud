package com.icuxika.framework.config.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

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
        // content-disposition: attachment; filename*=UTF-8''%E7%AE%80%E5%8E%86.md
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build());
        // content-disposition: attachment; filename="%E7%AE%80%E5%8E%86.md"
//        headers.setContentDisposition(ContentDisposition.attachment().filename(URLEncoder.encode(filename, StandardCharsets.UTF_8)).build());
        headers.toSingleValueMap().forEach(response::addHeader);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // Content-Type: application/octet-stream
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        consumer.accept(response.getOutputStream());
    }
}
