package com.examCrux.webApp.controller.courses;

import com.examCrux.webApp.dto.Response;
import com.examCrux.webApp.dto.course.CourseRequest;
import com.examCrux.webApp.dto.course.CourseResponse;
import com.examCrux.webApp.service.courses.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CoursesController {
    @Autowired
    CoursesService coursesService;

    @GetMapping("/{departmentId}")
    public ResponseEntity<Response<List<CourseResponse>>> list(@PathVariable Long departmentId, @RequestParam(required = false) String sem){
        return ResponseEntity.ok(coursesService.list(departmentId, sem));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coursesService.create(request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(coursesService.delete(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coursesService.update(id, request));
    }
}
