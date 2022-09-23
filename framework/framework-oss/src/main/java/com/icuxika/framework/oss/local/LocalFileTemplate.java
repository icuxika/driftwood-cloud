package com.icuxika.framework.oss.local;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.icuxika.framework.oss.core.FileProperties;
import com.icuxika.framework.oss.core.FileTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocalFileTemplate implements FileTemplate {

    private FileProperties fileProperties;

    public LocalFileTemplate(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    @Override
    public void createBucket(String bucketName) {
        File file = new File(fileProperties.getLocal().getBase() + FILE_SEPARATOR + bucketName);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("创建Bucket失败");
            }
        }
    }

    @Override
    public List<Bucket> getAllBuckets() {
        File root = new File(fileProperties.getLocal().getBase());
        File[] buckets = root.listFiles();
        if (buckets == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(buckets).filter(File::isDirectory).map(directory -> new Bucket(directory.getName())).collect(Collectors.toList());
    }

    @Override
    public void removeBucket(String bucketName) {
        try {
            Files.deleteIfExists(Paths.get(fileProperties.getLocal().getBase() + FILE_SEPARATOR + bucketName));
        } catch (IOException e) {
            throw new RuntimeException("删除Bucket失败");
        }
    }

    @Override
    public void putObject(String bucketName, String objectName, InputStream inputStream, String contextType) {
        String bucketPath = fileProperties.getLocal().getBase() + FILE_SEPARATOR + bucketName;
        File bucket = new File(bucketPath);
        if (!bucket.exists()) {
            createBucket(bucketName);
        }
        File file = new File(bucketPath + FILE_SEPARATOR + objectName);
        try {
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败");
        }
    }

    @Override
    public void putObject(String bucketName, String objectName, InputStream inputStream) {
        putObject(bucketName, objectName, inputStream, null);
    }

    @Override
    public S3Object getObject(String bucketName, String objectName) {
        File file = new File(fileProperties.getLocal().getBase() + FILE_SEPARATOR + bucketName + FILE_SEPARATOR + objectName);
        S3Object s3Object = new S3Object();
        try {
            s3Object.setObjectContent(new FileInputStream(file));
            return s3Object;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public void removeObject(String bucketName, String objectName) {
        try {
            Files.deleteIfExists(Paths.get(fileProperties.getLocal().getBase() + FILE_SEPARATOR + bucketName + FILE_SEPARATOR + objectName));
        } catch (IOException e) {
            throw new RuntimeException("删除文件失败");
        }
    }

    @Override
    public List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) {
        File root = new File(fileProperties.getLocal().getBase() + FILE_SEPARATOR + bucketName);
        File[] files = root.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(files).filter(file -> file.getName().startsWith(prefix)).map(file -> {
            S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
            s3ObjectSummary.setKey(file.getName());
            return s3ObjectSummary;
        }).collect(Collectors.toList());
    }
}
