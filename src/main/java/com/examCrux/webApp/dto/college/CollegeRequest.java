package com.examCrux.webApp.dto.college;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollegeRequest {
    private String collegeName;
    private String collegeAddress;
    private String collegeCode;
}
