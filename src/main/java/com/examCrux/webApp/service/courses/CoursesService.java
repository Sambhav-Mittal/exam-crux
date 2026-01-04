package com.examCrux.webApp.service.courses;

import com.examCrux.webApp.dto.Response;
import com.examCrux.webApp.dto.course.CourseRequest;
import com.examCrux.webApp.dto.course.CourseResponse;
import com.examCrux.webApp.entities.Courses;
import com.examCrux.webApp.entities.Departments;
import com.examCrux.webApp.exception.CourseNotFoundException;
import com.examCrux.webApp.exception.DepartmentNotFoundException;
import com.examCrux.webApp.repository.CoursesRepository;
import com.examCrux.webApp.repository.DepartmentsRepository;
import com.examCrux.webApp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoursesService {
    @Autowired
    CoursesRepository coursesRepository;
    @Autowired
    DepartmentsRepository departmentsRepository;
    @Autowired
    JwtUtil jwtUtil;

    public Response<List<CourseResponse>> list(Long departmentId, String sem){
        Departments departments = departmentsRepository.findById(departmentId).orElseThrow(() -> new DepartmentNotFoundException("Department Not Found"));
        List<CourseResponse> response;
        if (sem == null) {
            response =  coursesRepository.findByDepartments(departments)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }else{
            response =  coursesRepository.findByDepartmentsAndSem(departments, sem)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        return Response.<List<CourseResponse>>builder()
                .data(response)
                .httpStatusCode(HttpStatus.OK.value())
                .message("Courses fetched successfully!").build();
    }

    public Response<CourseResponse> create(CourseRequest request) {
        jwtUtil.ensureAdmin();
        Departments departments = departmentsRepository.findById(request.getDepartmentId()).orElseThrow(() -> new DepartmentNotFoundException("Department Not Found"));
        Courses courses = Courses.builder()
                .name(request.getName())
                .courseCode(request.getCourseCode())
                .sem(request.getSem())
                .departments(departments)
                .build();
        coursesRepository.save(courses);
        return Response.<CourseResponse>builder()
                .data(toResponse(courses)).httpStatusCode(201).message("Course Created Successfully!").build();
    }

    public Response<CourseResponse> update(Long id, CourseRequest request) {
        jwtUtil.ensureAdmin();
        Courses courses = coursesRepository.findById(id).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        if (request.getName() != null && !request.getName().equals(courses.getName())) {
            courses.setName(request.getName());
        }
        if (request.getCourseCode() != null && !request.getCourseCode().equals(courses.getCourseCode())) {
            courses.setCourseCode(request.getCourseCode());
        }
        if (request.getSem() != null && !request.getSem().equals(courses.getSem())) {
            courses.setSem(request.getSem());
        }
        if(request.getDepartmentId()!=null){
            Departments department = departmentsRepository.findById(request.getDepartmentId()).orElseThrow(()-> new DepartmentNotFoundException("Department Not Found"));
            courses.setDepartments(department);
        }
        coursesRepository.save(courses);

        return Response.<CourseResponse>builder()
                .data(toResponse(courses)).httpStatusCode(201).message("Course Updated Successfully!" + id).build();

    }

    public Response<?> delete(Long id) {
        jwtUtil.ensureAdmin();
        Courses courses = coursesRepository.findById(id).orElseThrow(()-> new CourseNotFoundException("Course not found"));
        coursesRepository.delete(courses);
        return Response.builder().httpStatusCode(200).message("Course Deleted Successfully!" + id).build();
    }

    private CourseResponse toResponse(Courses courses){
        return CourseResponse.builder()
                .id(courses.getId())
                .name(courses.getName())
                .courseCode(courses.getCourseCode())
                .sem(courses.getSem())
                .departmentName(courses.getDepartments().getName())
                .createdAt(courses.getCreatedAt())
                .updatedAt(courses.getUpdatedAt())
                .build();
    }
}
