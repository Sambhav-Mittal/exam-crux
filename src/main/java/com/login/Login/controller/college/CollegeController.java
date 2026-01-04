package com.login.Login.controller.college;

import com.login.Login.dto.Response;
import com.login.Login.dto.college.CollegeResponse;
import com.login.Login.dto.departments.CollegeRequest;
import com.login.Login.service.college.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/college")
public class CollegeController {
    @Autowired
    CollegeService collegeService;

    @GetMapping
    public ResponseEntity<Response<List<CollegeResponse>>> list() {
        return ResponseEntity.ok(collegeService.list());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CollegeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collegeService.create(request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(collegeService.delete(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody CollegeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collegeService.update(id, request));
    }
}
