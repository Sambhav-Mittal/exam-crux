package com.login.Login.dto.college;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CollegeResponse {
    private Long id;
    private String collegeName;
    private String collegeAddress;
    private String collegeCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
