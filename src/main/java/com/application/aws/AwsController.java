package com.application.aws;


import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.application.aws.dto.FileResponseDto;
import com.application.aws.service.S3FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class AwsController {

    private final S3FileService s3FileService;

    @Autowired
    public AwsController(S3FileService s3FileService) {
        this.s3FileService = s3FileService;
    }

    @PostMapping("/upload/{userId}")
    public ResponseEntity<FileResponseDto> uploadFile(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file) {

        try {
            String fileKey = s3FileService.uploadFile(userId, file);
            FileResponseDto response = new FileResponseDto(
                    file.getOriginalFilename(),
                    fileKey,
                    file.getContentType(),
                    file.getSize()
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/link/{userId}/{fileName}")
    public ResponseEntity<String> getFileLink(
            @PathVariable String userId,
            @PathVariable String fileName,
            @RequestParam(defaultValue = "5") int expirationMinutes) {

        try {
            String url = s3FileService.generatePreSignedUrl(userId, fileName, expirationMinutes);
            return new ResponseEntity<>(url, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{userId}/{fileName}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String userId,
            @PathVariable String fileName) {

        boolean isDeleted = s3FileService.deleteFile(userId, fileName);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<List<String>> listUserFiles(@PathVariable String userId) {
        List<String> files = s3FileService.listUserFiles(userId);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}
