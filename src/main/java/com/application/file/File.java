package com.application.file;

import com.application.common.StorageItem;
import com.application.folder.Folder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "files")
public class File extends StorageItem {
    @Column(nullable = false)
    private String s3Key;

    private long size;

    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_uuid", referencedColumnName = "uuid", nullable = false)
    private Folder folder;


    public File(String name, String userUuid, String s3Key, long size, String contentType, Folder folder) {
        this.setName(name);
        this.setUserUuid(userUuid);
        this.s3Key = s3Key;
        this.size = size;
        this.contentType = contentType;
        this.folder = folder;
    }

    // Get full path including filename
    public String getFullPath() {
        return folder.getPath() + getName();
    }
}