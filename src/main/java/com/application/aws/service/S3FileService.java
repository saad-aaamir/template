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

    /**
     * Upload file with a specific path
     */
    public String uploadFileWithPath(String folderPath, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Ensure folder path ends with a slash
        if (!folderPath.endsWith("/")) {
            folderPath += "/";
        }

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

    /**
     * Upload file with user's UUID as the root folder (original method)
     */
    public String uploadFile(String userUuid, MultipartFile file) throws IOException {
        return uploadFileWithPath(userUuid + "/", file);
    }

    /**
     * Get S3 object by key
     */
    public S3Object getFile(String key) {
        if (!fileExists(key)) {
            throw new RuntimeException("File not found with key: " + key);
        }

        return amazonS3.getObject(bucketName, key);
    }

    /**
     * Delete file by key
     */
    public boolean deleteFile(String key) {
        if (!fileExists(key)) {
            return false;
        }

        amazonS3.deleteObject(bucketName, key);
        return true;
    }

    /**
     * List files in a specific path/prefix
     */
    public List<String> listFilesInPath(String prefix) {
        // Ensure prefix ends with a slash
        if (!prefix.endsWith("/")) {
            prefix += "/";
        }

        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(prefix)
                .withDelimiter("/");

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        List<String> fileNames = new ArrayList<>();

        // Get files in current directory
        for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
            String key = objectSummary.getKey();
            if (!key.equals(prefix)) {
                // Extract just the filename
                fileNames.add(key.substring(prefix.length()));
            }
        }

        return fileNames;
    }

    /**
     * List subdirectories in a specific path/prefix
     */
    public List<String> listSubdirectoriesInPath(String prefix) {
        // Ensure prefix ends with a slash
        if (!prefix.endsWith("/")) {
            prefix += "/";
        }

        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(prefix)
                .withDelimiter("/");

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        List<String> subDirectories = new ArrayList<>();

        // Get subdirectories
        for (String commonPrefix : result.getCommonPrefixes()) {
            // Remove the prefix and trailing slash to get just the directory name
            String dirName = commonPrefix.substring(prefix.length(), commonPrefix.length() - 1);
            subDirectories.add(dirName);
        }

        return subDirectories;
    }

    /**
     * Generate pre-signed URL for a file
     */
    public String generatePreSignedUrl(String key, int expirationTimeInMinutes) {
        if (!fileExists(key)) {
            throw new RuntimeException("File not found: " + key);
        }

        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000L * 60 * expirationTimeInMinutes;
        expiration.setTime(expTimeMillis);

        return amazonS3.generatePresignedUrl(bucketName, key, expiration).toString();
    }

    /**
     * Check if a file exists
     */
    public boolean fileExists(String key) {
        return amazonS3.doesObjectExist(bucketName, key);
    }
}