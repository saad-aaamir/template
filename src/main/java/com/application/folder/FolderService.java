package com.application.folder;

import com.amazonaws.services.kms.model.NotFoundException;
import com.application.file.File;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class FolderService {
    private final FolderRepository folderRepository;

    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Transactional
    public Folder createRootFolder(String name, String userUuid) {
        Folder rootFolder = new Folder(name, userUuid);
        return folderRepository.save(rootFolder);
    }

    @Transactional
    public Folder createSubFolder(String name, String userUuid, String parentFolderUuid) throws AccessDeniedException {
        Folder parentFolder = folderRepository.findByUuid(parentFolderUuid)
                .orElseThrow(() -> new NotFoundException("Parent folder not found"));

        // Verify user has access to this folder
        if (!parentFolder.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this folder");
        }

        Folder subFolder = new Folder(name, userUuid, parentFolder);
        parentFolder.addSubFolder(subFolder);

        return folderRepository.save(subFolder);
    }

    public List<Folder> getUserRootFolders(String userUuid) {
        return folderRepository.findByUserUuidAndParentFolderIsNull(userUuid);
    }

    public Folder getFolderByUuid(String userUuid, String folderUuid) throws AccessDeniedException {
        Folder folder = folderRepository.findByUuid(folderUuid)
                .orElseThrow(() -> new NotFoundException("Folder not found"));

        // Verify user has access to this folder
        if (!folder.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this folder");
        }

        return folder;
    }

    public List<Folder> getSubFolders(String userUuid, String folderUuid) throws AccessDeniedException {
        Folder folder = folderRepository.findByUuid(folderUuid)
                .orElseThrow(() -> new NotFoundException("Folder not found"));

        // Verify user has access to this folder
        if (!folder.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this folder");
        }

        return folder.getSubFolders();
    }

    @Transactional
    public void deleteFolder(String userUuid, String folderUuid) throws AccessDeniedException {
        Folder folder = folderRepository.findByUuid(folderUuid)
                .orElseThrow(() -> new NotFoundException("Folder not found"));

        // Verify user has access to this folder
        if (!folder.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this folder");
        }

        // Remove from parent's subfolders if it has a parent
        if (folder.getParentFolder() != null) {
            folder.getParentFolder().getSubFolders().remove(folder);
        }

        folderRepository.delete(folder);
    }

    @Transactional
    public Folder moveFolder(String userUuid, String folderUuid, String destinationFolderUuid) throws AccessDeniedException {
        if (folderUuid.equals(destinationFolderUuid)) {
            throw new IllegalArgumentException("Cannot move folder into itself");
        }

        Folder folder = folderRepository.findByUuid(folderUuid)
                .orElseThrow(() -> new NotFoundException("Folder not found"));

        Folder destinationFolder = folderRepository.findByUuid(destinationFolderUuid)
                .orElseThrow(() -> new NotFoundException("Destination folder not found"));

        // Verify user has access to both folders
        if (!folder.getUserUuid().equals(userUuid) || !destinationFolder.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this resource");
        }

        // Check if destination is not a child of the folder being moved
        Folder parent = destinationFolder;
        while (parent != null) {
            if (parent.getUuid().equals(folder.getUuid())) {
                throw new IllegalArgumentException("Cannot move a folder into its own subfolder");
            }
            parent = parent.getParentFolder();
        }

        // Remove from current parent if exists
        if (folder.getParentFolder() != null) {
            folder.getParentFolder().getSubFolders().remove(folder);
        }

        // Add to new parent
        destinationFolder.addSubFolder(folder);
        folder.setParentFolder(destinationFolder);

        return folderRepository.save(folder);
    }

    @Transactional
    public Folder renameFolder(String userUuid, String folderUuid, String newName) throws AccessDeniedException {
        Folder folder = folderRepository.findByUuid(folderUuid)
                .orElseThrow(() -> new NotFoundException("Folder not found"));

        // Verify user has access to this folder
        if (!folder.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this folder");
        }

        folder.setName(newName);
        return folderRepository.save(folder);
    }

    public FolderDetailsDTO getFolderDetails(String userUuid, String folderUuid) throws AccessDeniedException {
        Folder folder = folderRepository.findByUuid(folderUuid)
                .orElseThrow(() -> new NotFoundException("Folder not found"));

        // Verify user has access to this folder
        if (!folder.getUserUuid().equals(userUuid)) {
            throw new AccessDeniedException("You don't have access to this folder");
        }

        // Create DTO with counts and other details
        FolderDetailsDTO details = new FolderDetailsDTO();
        details.setFolder(folder);
        details.setSubFolderCount(folder.getSubFolders().size());
        details.setFileCount(folder.getFiles().size());

        // Calculate total size of all files in folder
        long totalSize = folder.getFiles().stream()
                .mapToLong(File::getSize)
                .sum();
        details.setTotalSize(totalSize);

        return details;
    }
}