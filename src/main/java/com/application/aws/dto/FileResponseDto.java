package com.application.aws.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileResponseDto {
    private String fileName;
    private String filePath;
    private String fileType;
    private long size;
}