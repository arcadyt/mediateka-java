package com.acowg.peer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaDto {
    private String id;
    private LocalDateTime createdAt;
    private String catalogId;
    private String relativeFilePath;
    private int sizeInBytes;
    private DirectoryDto directory;
}