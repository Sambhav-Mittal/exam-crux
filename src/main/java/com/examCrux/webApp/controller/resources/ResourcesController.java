package com.examCrux.webApp.controller.resources;

import com.examCrux.webApp.dto.Response;
import com.examCrux.webApp.dto.resource.ResourceResponse;
import com.examCrux.webApp.entities.ResourceType;
import com.examCrux.webApp.service.resources.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@RestController
@RequestMapping("/resources")
public class ResourcesController {
    @Autowired
    ResourcesService resourceService;



    @PostMapping("/upload")
    public ResponseEntity<Response<?>> uploadResource(
            @RequestParam(required = false) Long resourceId,
            @RequestParam String name,
            @RequestParam ResourceType type,
            @RequestParam Long courseId,
            @RequestParam(required = false) MultipartFile file,  // Only needed if type is "file"
            @RequestParam(required = false) String youtubeUrl) throws IOException {  // Only needed if type is "video"

        return ResponseEntity.ok(resourceService.createOrUpdateResource(resourceId, name, type, courseId, file, youtubeUrl));
    }

    // Get all resources (CRUD)
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Response<?>> getAllResources(@PathVariable Long courseId) {
        return ResponseEntity.ok(resourceService.getResourcesByCourse(courseId));
    }

    // Get resource by ID (CRUD)
    @GetMapping("/{id}")
    public Response<ResourceResponse> getResourceById(@PathVariable Long id) {

        return resourceService.getResourceById(id);
    }


    // Delete resource (CRUD)
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteResource(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(resourceService.deleteResource(id));
    }

}

