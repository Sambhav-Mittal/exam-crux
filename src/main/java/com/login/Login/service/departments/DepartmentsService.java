package com.login.Login.service.departments;

import com.login.Login.dto.Response;
import com.login.Login.dto.departments.DepartmentRequest;
import com.login.Login.dto.departments.DepartmentResponse;
import com.login.Login.entities.College;
import com.login.Login.entities.Departments;
import com.login.Login.exception.CollegeNotFoundException;
import com.login.Login.exception.DepartmentNotFoundException;
import com.login.Login.repository.CollegeRepository;
import com.login.Login.repository.DepartmentsRepository;
import com.login.Login.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentsService {
    @Autowired
    DepartmentsRepository departmentsRepository;
    @Autowired
    CollegeRepository collegeRepository;
    @Autowired
    JwtUtil jwtUtil;

    public Response<List<DepartmentResponse>> list() {
        List<DepartmentResponse> responses = departmentsRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
        return Response.<List<DepartmentResponse>>builder()
                .data(responses)
                .httpStatusCode(HttpStatus.OK.value())
                .message("Departments fetched successfully!").build();
    }

    public Response<DepartmentResponse> create(DepartmentRequest request) {
        jwtUtil.ensureAdmin();
        College college = collegeRepository.findById(request.getCollegeId()).orElseThrow(()-> new CollegeNotFoundException("College Not Found"));
        Departments departments = Departments.builder()
                .college(college)
                .name(request.getName())
                .branchCode(request.getBranchCode()).build();
        departmentsRepository.save(departments);
        return Response.<DepartmentResponse>builder()
                .data(toResponse(departments)).httpStatusCode(201).message("Department Created Successfully!").build();
    }

    public Response<DepartmentResponse> update(Long id, DepartmentRequest request) {
        jwtUtil.ensureAdmin();
        Departments departments = departmentsRepository.findById(id).orElseThrow(() -> new DepartmentNotFoundException("Department not found"));
        if (request.getName() !=null && !request.getName().equals(departments.getName())) {
            departments.setName(request.getName());
        }
        if (request.getBranchCode() != null && !request.getBranchCode().equals(departments.getBranchCode())) {
            departments.setBranchCode(request.getBranchCode());
        }
        departmentsRepository.save(departments);

        return Response.<DepartmentResponse>builder()
                .data(toResponse(departments)).httpStatusCode(201).message("Department Updated Successfully!" + id).build();

    }

    public Response<?> delete(Long id) {
        jwtUtil.ensureAdmin();
        Departments departments = departmentsRepository.findById(id).orElseThrow(()-> new DepartmentNotFoundException("Department not found"));
        departmentsRepository.delete(departments);
        return Response.builder().httpStatusCode(200).message("Department Deleted Successfully!" + id).build();
    }

    private DepartmentResponse toResponse(Departments departments) {

        return DepartmentResponse.builder()
                .id(departments.getId())
                .departmentName(departments.getName())
                .collegeName(departments.getCollege().getName())
                .branchCode(departments.getBranchCode())
                .createdAt(departments.getCreatedAt())
                .updatedAt(departments.getUpdatedAt())
                .build();
    }
}
