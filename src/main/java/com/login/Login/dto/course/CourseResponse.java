package com.login.Login.dto.course;

import com.login.Login.entities.Departments;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CourseResponse {
    private Long id;
    private String name;
    private String courseCode;
    private String sem;
    private String departmentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
