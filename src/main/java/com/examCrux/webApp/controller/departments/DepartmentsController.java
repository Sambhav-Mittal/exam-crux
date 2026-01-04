package com.examCrux.webApp.controller.departments;


import com.examCrux.webApp.dto.Response;
import com.examCrux.webApp.dto.departments.DepartmentRequest;
import com.examCrux.webApp.dto.departments.DepartmentResponse;
import com.examCrux.webApp.service.departments.DepartmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentsController {
    @Autowired
    DepartmentsService departmentsService;

    @GetMapping
    public ResponseEntity<Response<List<DepartmentResponse>>> list() {
        return ResponseEntity.ok(departmentsService.list());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentsService.create(request));

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(departmentsService.delete(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentsService.update(id, request));
    }
}