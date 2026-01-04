package com.examCrux.webApp.service.college;

import com.examCrux.webApp.dto.Response;
import com.examCrux.webApp.dto.college.CollegeResponse;
import com.examCrux.webApp.dto.college.CollegeRequest;
import com.examCrux.webApp.entities.College;
import com.examCrux.webApp.exception.CollegeNotFoundException;
import com.examCrux.webApp.repository.CollegeRepository;
import com.examCrux.webApp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollegeService {
    @Autowired
    CollegeRepository collegeRepository;
    @Autowired
    JwtUtil jwtUtil;

    public Response<List<CollegeResponse>> list() {
        List<CollegeResponse> responses = collegeRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
        return Response.<List<CollegeResponse>>builder()
                .data(responses)
                .httpStatusCode(HttpStatus.OK.value())
                .message("College fetched successfully!").build();
    }

    public Response<CollegeResponse> create(CollegeRequest request) {
        jwtUtil.ensureAdmin();
        College college = College.builder()
                .name(request.getCollegeName())
                .collegeCode(request.getCollegeCode())
                .address(request.getCollegeAddress()).build();
        collegeRepository.save(college);
        return Response.<CollegeResponse>builder()
                .data(toResponse(college)).httpStatusCode(201).message("College Created Successfully!").build();
    }

    public Response<CollegeResponse> update(Long id, CollegeRequest request) {
        jwtUtil.ensureAdmin();
        College college = collegeRepository.findById(id).orElseThrow(()-> new CollegeNotFoundException("Collage Not Found!"));
        if (request.getCollegeName() !=null && !request.getCollegeName().equals(college.getName())) {
            college.setName(request.getCollegeName());
        }
        if (request.getCollegeAddress() !=null && !request.getCollegeAddress().equals(college.getAddress())) {
            college.setAddress(request.getCollegeAddress());
        }
        if (request.getCollegeCode() !=null && !request.getCollegeCode().equals(college.getCollegeCode())) {
            college.setCollegeCode(request.getCollegeCode());
        }

        collegeRepository.save(college);

        return Response.<CollegeResponse>builder()
                .data(toResponse(college)).httpStatusCode(201).message("College Updated Successfully!" + id).build();

    }

    public Response<?> delete(Long id) {
        jwtUtil.ensureAdmin();
        College college = collegeRepository.findById(id).orElseThrow(()-> new CollegeNotFoundException("College not found"));
        collegeRepository.delete(college);
        return Response.builder().httpStatusCode(200).message("College Deleted Successfully!" + id).build();
    }

    private CollegeResponse toResponse(College college) {

        return CollegeResponse.builder()
                .id(college.getId())
                .collegeCode(college.getCollegeCode())
                .collegeName(college.getName())
                .collegeAddress(college.getAddress())
                .createdAt(college.getCreatedAt())
                .updatedAt(college.getUpdatedAt())
                .build();
    }
}
