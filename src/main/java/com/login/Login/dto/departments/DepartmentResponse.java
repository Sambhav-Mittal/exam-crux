package com.login.Login.dto.departments;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DepartmentResponse {
    private Long id;
    private String collegeName;
    private String departmentName;
    private String branchCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
