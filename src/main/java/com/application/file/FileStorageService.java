package com.application.file;

import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.model.S3Object;
import com.application.aws.service.S3FileService;
import com.application.folder.Folder;
import com.application.folder.FolderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class FileStorageService {
    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final S3FileService s3FileService;

    public FileStorageService(FileRepository fileRepository, FolderRepository folderRepository, S3FileService s3FileService) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.s3FileService = s3FileService;
    }

    @Transactional
    public File uploadFile(String userUuid, String folderUuid, MultipartFile multipartFile) throws IOException {
        Folder folder = folderRepository.findByUuid(folderUuid)
                .orElseThrow(() -> new NotFoundException("Folder not found"));

        // Verify user has access to this folder
        if (!folder.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this folder");
        }

        // Generate S3 key based on folder path
        String folderPath = userUuid + folder.getPath();
        String originalFilename = multipartFile.getOriginalFilename();

        // Upload file to S3
        String s3Key = s3FileService.uploadFileWithPath(folderPath, multipartFile);

        // Create and save file entity
        File file = new File(
                originalFilename,
                userUuid,
                s3Key,
                multipartFile.getSize(),
                multipartFile.getContentType(),
                folder
        );

        folder.addFile(file);
        return fileRepository.save(file);
    }

    public File getFileByUuid(String userUuid, String fileUuid) throws AccessDeniedException {
        File file = fileRepository.findByUuid(fileUuid)
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Verify user has access to this file
        if (!file.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this file");
        }

        return file;
    }

    public List<File> getFilesInFolder(String userUuid, String folderUuid) throws AccessDeniedException {
        Folder folder = folderRepository.findByUuid(folderUuid)
                .orElseThrow(() -> new NotFoundException("Folder not found"));

        // Verify user has access to this folder
        if (!folder.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this folder");
        }

        return folder.getFiles();
    }

    public S3Object downloadFile(String userUuid, String fileUuid) throws AccessDeniedException {
        File file = fileRepository.findByUuid(fileUuid)
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Verify user has access to this file
        if (!file.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this file");
        }

        return s3FileService.getFile(file.getS3Key());
    }

    @Transactional
    public void deleteFile(String userUuid, String fileUuid) throws AccessDeniedException {
        File file = fileRepository.findByUuid(fileUuid)
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Verify user has access to this file
        if (!file.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this file");
        }

        // Delete from S3
        s3FileService.deleteFile(file.getS3Key());

        // Remove from folder's files list
        file.getFolder().getFiles().remove(file);

        // Delete file entity
        fileRepository.delete(file);
    }

    public String generateDownloadUrl(String userUuid, String fileUuid, int expirationMinutes) throws AccessDeniedException {
        File file = fileRepository.findByUuid(fileUuid)
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Verify user has access to this file
        if (!file.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this file");
        }

        return s3FileService.generatePreSignedUrl(file.getS3Key(), expirationMinutes);
    }

    @Transactional
    public File moveFile(String userUuid, String fileUuid, String destinationFolderUuid) throws AccessDeniedException {
        File file = fileRepository.findByUuid(fileUuid)
                .orElseThrow(() -> new NotFoundException("File not found"));

        Folder destinationFolder = folderRepository.findByUuid(destinationFolderUuid)
                .orElseThrow(() -> new NotFoundException("Destination folder not found"));

        // Verify user has access to both file and folder
        if (!file.getUserUuid().equals(userUuid) || !destinationFolder.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this resource");
        }

        // Remove from current folder
        file.getFolder().getFiles().remove(file);

        // Add to new folder
        destinationFolder.addFile(file);
        file.setFolder(destinationFolder);

        return fileRepository.save(file);
    }

    @Transactional
    public File renameFile(String userUuid, String fileUuid, String newName) throws AccessDeniedException {
        File file = fileRepository.findByUuid(fileUuid)
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Verify user has access to this file
        if (!file.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this file");
        }

        file.setName(newName);
        return fileRepository.save(file);
    }
}