package com.login.Login.dto.course;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseRequest {
    private Long departmentId;
    private String name;
    private String courseCode;
    private String sem;

}
