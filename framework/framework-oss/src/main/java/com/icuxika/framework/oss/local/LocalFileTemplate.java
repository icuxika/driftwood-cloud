package com.icuxika.framework.oss.local;

import com.amazonaws.services.s3.model.*;
import com.icuxika.framework.oss.core.FileProperties;
import com.icuxika.framework.oss.core.FileTemplate;
import com.icuxika.framework.oss.core.FileUploadPart;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
            Path path = file.toPath();
            Files.createDirectories(path.getParent());
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败：" + e.getMessage());
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
            throw new RuntimeException("删除文件失败：" + e.getMessage());
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

    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String objectName) {
        File file = new File(fileProperties.getLocal().getBase() + FILE_SEPARATOR + bucketName + FILE_SEPARATOR + objectName);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.length());
        return objectMetadata;
    }

    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(String bucketName, String objectName) {
        String uuid = UUID.randomUUID().toString();
        String bucketPath = fileProperties.getLocal().getBase() + FILE_SEPARATOR + bucketName;
        File bucket = new File(bucketPath);
        if (!bucket.exists()) {
            createBucket(bucketName);
        }
        File file = new File(bucketPath + FILE_SEPARATOR + objectName);
        try {
            Path path = file.toPath();
            Path multiPartUploadPath = path.getParent().resolve(Path.of("multi_part_upload")).resolve(Path.of(uuid));
            Files.createDirectories(multiPartUploadPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        InitiateMultipartUploadResult initiateMultipartUploadResult = new InitiateMultipartUploadResult();
        initiateMultipartUploadResult.setUploadId(uuid);
        initiateMultipartUploadResult.setBucketName(bucketName);
        initiateMultipartUploadResult.setKey(objectName);
        return initiateMultipartUploadResult;
    }

    @Override
    public UploadPartResult uploadPart(String bucketName, String objectName, InputStream inputStream, FileUploadPart fileUploadPart) {
        String bucketPath = fileProperties.getLocal().getBase() + FILE_SEPARATOR + bucketName;
        File file = new File(bucketPath + FILE_SEPARATOR + objectName);
        Path multiPartUploadPath = file.toPath().getParent().resolve(Path.of("multi_part_upload").resolve(Path.of(fileUploadPart.getUploadId())));
        String tag = UUID.randomUUID().toString();
        Path partPath = multiPartUploadPath.resolve(fileUploadPart.getPartNumber() + "_" + tag + "_" + file.getName());
        try {
            Files.copy(inputStream, partPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UploadPartResult uploadPartResult = new UploadPartResult();
        uploadPartResult.setPartNumber(fileUploadPart.getPartNumber());
        uploadPartResult.setETag(tag);
        return uploadPartResult;
    }

    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(String bucketName, String objectName, String uploadId, List<PartETag> partETags) {
        String bucketPath = fileProperties.getLocal().getBase() + FILE_SEPARATOR + bucketName;
        File file = new File(bucketPath + FILE_SEPARATOR + objectName);
        Path multiPartUploadPath = file.toPath().getParent().resolve(Path.of("multi_part_upload").resolve(Path.of(uploadId)));
        List<Path> partPathList = partETags.stream().map(partETag -> multiPartUploadPath.resolve(partETag.getPartNumber() + "_" + partETag.getETag() + "_" + file.getName())).toList();
        boolean allPartFileExists = partPathList.stream().allMatch(partPath -> partPath.toFile().exists());
        if (!allPartFileExists) {
            throw new RuntimeException("已存在的分片文件数量不满足要求");
        }

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            partPathList.forEach(partPath -> {
                File partFile = partPath.toFile();
                try (FileInputStream inputStream = new FileInputStream(partFile)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Files.walkFileTree(multiPartUploadPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
//            Files.delete(multiPartUploadPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CompleteMultipartUploadResult completeMultipartUploadResult = new CompleteMultipartUploadResult();
        completeMultipartUploadResult.setBucketName(bucketName);
        completeMultipartUploadResult.setKey(objectName);
        return completeMultipartUploadResult;
    }

    @Override
    public MultipartUploadListing listMultipartUploads(String bucketName) {
        return null;
    }

    @Override
    public void abortMultipartUpload(String bucketName, String objectName, String uploadId) {

    }
}
