package com.application.file;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@AllArgsConstructor
@Tag(name = "File Management", description = "APIs for managing files")
public class FileController {
    private final FileStorageService fileStorageService;

    @PostMapping("/{folderUuid}/upload")
    @Operation(summary = "Upload file", description = "Uploads a file to the specified folder")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "File uploaded successfully"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "Folder not found")
    })
    public ResponseEntity<File> uploadFile(
            @PathVariable String folderUuid,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Uuid") String userUuid) throws IOException {
        File uploadedFile = fileStorageService.uploadFile(userUuid, folderUuid, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedFile);
    }

    @GetMapping("/{fileUuid}")
    @Operation(summary = "Get file by UUID", description = "Retrieves file metadata by UUID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved file metadata"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "File not found")
    })
    public ResponseEntity<File> getFileByUuid(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        File file = fileStorageService.getFileByUuid(userUuid, fileUuid);
        return ResponseEntity.ok(file);
    }

    @GetMapping("/folder/{folderUuid}")
    @Operation(summary = "List files in folder", description = "Lists all files in the specified folder")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved files"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "Folder not found")
    })
    public ResponseEntity<List<File>> getFilesInFolder(
            @PathVariable String folderUuid,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        List<File> files = fileStorageService.getFilesInFolder(userUuid, folderUuid);
        return ResponseEntity.ok(files);
    }


    @DeleteMapping("/{fileUuid}")
    @Operation(summary = "Delete file", description = "Deletes a file permanently")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "File deleted successfully"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "File not found")
    })
    public ResponseEntity<Void> deleteFile(
            @PathVariable String fileUuid,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        fileStorageService.deleteFile(userUuid, fileUuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{fileUuid}/download-url")
    @Operation(summary = "Generate download URL", description = "Generates a pre-signed URL for file download")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "URL generated successfully"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "File not found")
    })
    public ResponseEntity<Map<String, String>> generateDownloadUrl(
            @PathVariable String fileUuid,
            @RequestParam(defaultValue = "60") int expirationMinutes,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        String url = fileStorageService.generateDownloadUrl(userUuid, fileUuid, expirationMinutes);
        Map<String, String> response = Map.of("downloadUrl", url);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{fileUuid}/move/{destinationFolderUuid}")
    @Operation(summary = "Move file", description = "Moves a file to a new destination folder")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File moved successfully"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "File or folder not found")
    })
    public ResponseEntity<File> moveFile(
            @PathVariable String fileUuid,
            @PathVariable String destinationFolderUuid,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        File file = fileStorageService.moveFile(userUuid, fileUuid, destinationFolderUuid);
        return ResponseEntity.ok(file);
    }

    @PatchMapping("/{fileUuid}/rename")
    @Operation(summary = "Rename file", description = "Renames a file")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File renamed successfully"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "File not found")
    })
    public ResponseEntity<File> renameFile(
            @PathVariable String fileUuid,
            @RequestBody @Valid RenameFileRequest request,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        File file = fileStorageService.renameFile(userUuid, fileUuid, request.getNewName());
        return ResponseEntity.ok(file);
    }
}