package com.application.aws.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3FileService {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3FileService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadFile(String userUuid, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Generate folder path based on user's UUID
        String folderPath = userUuid + "/";

        // Create full key path with filename
        String key = folderPath + file.getOriginalFilename();

        // Upload file
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(
                new PutObjectRequest(bucketName, key, file.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.Private)
        );

        return key;
    }

    public S3Object getFile(String userUuid, String fileName) {
        String key = userUuid + "/" + fileName;

        if (!fileExists(key)) {
            throw new RuntimeException("File not found: " + fileName);
        }

        return amazonS3.getObject(bucketName, key);
    }

    public boolean deleteFile(String userUuid, String fileName) {
        String key = userUuid + "/" + fileName;

        if (!fileExists(key)) {
            return false;
        }

        amazonS3.deleteObject(bucketName, key);
        return true;
    }

    public List<String> listUserFiles(String userUuid) {
        String prefix = userUuid + "/";
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(prefix)
                .withDelimiter("/");

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        List<String> fileNames = new ArrayList<>();

        for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
            // Remove the prefix to get just the filename
            String key = objectSummary.getKey();
            if (!key.equals(prefix)) {
                fileNames.add(key.substring(prefix.length()));
            }
        }

        return fileNames;
    }

    public String generatePreSignedUrl(String userUuid, String fileName, int expirationTimeInMinutes) {
        String key = userUuid + "/" + fileName;

        if (!fileExists(key)) {
            throw new RuntimeException("File not found: " + fileName);
        }

        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000L * 60 * expirationTimeInMinutes;
        expiration.setTime(expTimeMillis);

        return amazonS3.generatePresignedUrl(bucketName, key, expiration).toString();
    }

    private boolean fileExists(String key) {
        return amazonS3.doesObjectExist(bucketName, key);
    }
}
