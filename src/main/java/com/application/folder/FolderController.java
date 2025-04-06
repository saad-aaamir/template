package com.application.folder;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
@Tag(name = "Folder Management", description = "APIs for managing folders")
public class FolderController {
    private final FolderService folderService;

    @PostMapping("/root")
    @Operation(summary = "Create root folder", description = "Creates a new root folder for the user")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Folder created successfully"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 401, message = "Unauthorized access")
    })
    public ResponseEntity<Folder> createRootFolder(
            @RequestBody @Valid CreateFolderRequest request,
            @RequestHeader("X-User-Uuid") String userUuid) {
        Folder folder = folderService.createRootFolder(request.getName(), userUuid);
        return ResponseEntity.status(HttpStatus.CREATED).body(folder);
    }

    @PostMapping("/{parentFolderUuid}/subfolders")
    @Operation(summary = "Create subfolder", description = "Creates a new subfolder under the specified parent folder")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Subfolder created successfully"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "Parent folder not found")
    })
    public ResponseEntity<Folder> createSubFolder(
            @PathVariable String parentFolderUuid,
            @RequestBody @Valid CreateFolderRequest request,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        Folder folder = folderService.createSubFolder(request.getName(), userUuid, parentFolderUuid);
        return ResponseEntity.status(HttpStatus.CREATED).body(folder);
    }

    @GetMapping("/root")
    @Operation(summary = "Get root folders", description = "Retrieves all root folders for the user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved root folders"),
            @ApiResponse(code = 401, message = "Unauthorized access")
    })
    public ResponseEntity<List<Folder>> getUserRootFolders(
            @RequestHeader("X-User-Uuid") String userUuid) {
        List<Folder> folders = folderService.getUserRootFolders(userUuid);
        return ResponseEntity.ok(folders);
    }

    @GetMapping("/{folderUuid}")
    @Operation(summary = "Get folder by UUID", description = "Retrieves folder details by UUID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved folder"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "Folder not found")
    })
    public ResponseEntity<Folder> getFolderByUuid(
            @PathVariable String folderUuid,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        Folder folder = folderService.getFolderByUuid(userUuid, folderUuid);
        return ResponseEntity.ok(folder);
    }

    @GetMapping("/{folderUuid}/subfolders")
    @Operation(summary = "Get subfolders", description = "Retrieves all subfolders for a specific folder")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved subfolders"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "Folder not found")
    })
    public ResponseEntity<List<Folder>> getSubFolders(
            @PathVariable String folderUuid,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        List<Folder> subfolders = folderService.getSubFolders(userUuid, folderUuid);
        return ResponseEntity.ok(subfolders);
    }

    @DeleteMapping("/{folderUuid}")
    @Operation(summary = "Delete folder", description = "Deletes a folder and all its contents")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Folder deleted successfully"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "Folder not found")
    })
    public ResponseEntity<Void> deleteFolder(
            @PathVariable String folderUuid,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        folderService.deleteFolder(userUuid, folderUuid);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{folderUuid}/move/{destinationFolderUuid}")
    @Operation(summary = "Move folder", description = "Moves a folder to a new destination folder")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Folder moved successfully"),
            @ApiResponse(code = 400, message = "Invalid operation"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "Folder not found")
    })
    public ResponseEntity<Folder> moveFolder(
            @PathVariable String folderUuid,
            @PathVariable String destinationFolderUuid,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        Folder folder = folderService.moveFolder(userUuid, folderUuid, destinationFolderUuid);
        return ResponseEntity.ok(folder);
    }

    @PatchMapping("/{folderUuid}/rename")
    @Operation(summary = "Rename folder", description = "Renames a folder")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Folder renamed successfully"),
            @ApiResponse(code = 400, message = "Invalid input data"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "Folder not found")
    })
    public ResponseEntity<Folder> renameFolder(
            @PathVariable String folderUuid,
            @RequestBody @Valid RenameFolderRequest request,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        Folder folder = folderService.renameFolder(userUuid, folderUuid, request.getNewName());
        return ResponseEntity.ok(folder);
    }

    @GetMapping("/{folderUuid}/details")
    @Operation(summary = "Get folder details", description = "Retrieves detailed information about a folder")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved folder details"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 404, message = "Folder not found")
    })
    public ResponseEntity<FolderDetailsDTO> getFolderDetails(
            @PathVariable String folderUuid,
            @RequestHeader("X-User-Uuid") String userUuid) throws AccessDeniedException {
        FolderDetailsDTO details = folderService.getFolderDetails(userUuid, folderUuid);
        return ResponseEntity.ok(details);
    }
}
