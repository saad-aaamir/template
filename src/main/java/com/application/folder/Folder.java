package com.application.folder;

import com.application.common.StorageItem;
import com.application.file.File;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "folders")
public class Folder extends StorageItem {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_uuid", referencedColumnName = "uuid")
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> subFolders = new ArrayList<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    // Root folder constructor
    public Folder(String name, String userUuid) {
        this.setName(name);
        this.setUserUuid(userUuid);
        this.parentFolder = null;
        this.subFolders = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    // Subfolder constructor
    public Folder(String name, String userUuid, Folder parentFolder) {
        this.setName(name);
        this.setUserUuid(userUuid);
        this.parentFolder = parentFolder;
        this.subFolders = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    // Add subfolder
    public void addSubFolder(Folder folder) {
        subFolders.add(folder);
        folder.setParentFolder(this);
    }

    // Add file
    public void addFile(File file) {
        files.add(file);
        file.setFolder(this);
    }

    // Get full path
    public String getPath() {
        if (parentFolder == null) {
            return "/" + getName() + "/";
        }
        return parentFolder.getPath() + getName() + "/";
    }
}