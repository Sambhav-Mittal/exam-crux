package com.examCrux.webApp.dto.resource;

import com.examCrux.webApp.entities.ResourceType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResourceResponse {
    private Long id;
    private String name;
    private ResourceType type;
    private String pathUrl;
    private String courseName;
    private String uploadedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
