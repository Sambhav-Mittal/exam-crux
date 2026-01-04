package com.login.Login.dto.resource;

import com.login.Login.entities.Courses;
import com.login.Login.entities.ResourceType;
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
