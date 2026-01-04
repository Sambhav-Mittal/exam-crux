package com.examCrux.webApp.dto.departments;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentRequest {
    private Long collegeId;
    private String name;
    private String branchCode;
}
