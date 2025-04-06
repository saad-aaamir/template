package com.application.folder;

import lombok.Data;

@Data
public class FolderDetailsDTO {
    private Folder folder;
    private int subFolderCount;
    private int fileCount;
    private long totalSize; // in bytes

    public String getFormattedTotalSize() {
        if (totalSize < 1024) {
            return totalSize + " B";
        } else if (totalSize < 1024 * 1024) {
            return String.format("%.2f KB", totalSize / 1024.0);
        } else if (totalSize < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", totalSize / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", totalSize / (1024.0 * 1024 * 1024));
        }
    }
}
