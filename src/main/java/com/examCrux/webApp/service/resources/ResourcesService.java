package com.examCrux.webApp.service.resources;

import com.examCrux.webApp.dto.Response;
import com.examCrux.webApp.dto.resource.ResourceResponse;
import com.examCrux.webApp.entities.*;
import com.login.webApp.entities.*;
import com.examCrux.webApp.exception.CourseNotFoundException;
import com.examCrux.webApp.exception.ResourceNotFoundException;
import com.examCrux.webApp.repository.CoursesRepository;
import com.examCrux.webApp.repository.ResourcesRepository;
import com.examCrux.webApp.repository.UserRepository;
import com.examCrux.webApp.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourcesService {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    ResourcesRepository resourceRepository;
    @Autowired
    CoursesRepository courseRepository;
    @Autowired
    UserRepository userRepository;
    @Value("${upload.dir}")  // Directory to store uploaded files (can be set in application.properties)
    String uploadDir;

    public Response<List<ResourceResponse>> getResourcesByCourse(Long courseId) {
        Courses courses = courseRepository.findById(courseId).orElseThrow(()-> new CourseNotFoundException("Course Not Found"));
        List<ResourceResponse> responses = resourceRepository.findByCourses(courses)
                .stream()
                .map(this::toResponse)  // Use the toResponse method here
                .collect(Collectors.toList());
        return Response.<List<ResourceResponse>>builder()
                .data(responses)
                .httpStatusCode(HttpStatus.OK.value())
                .message("Resources fetched successfully!").build();

    }

    public Response<ResourceResponse> getResourceById(Long id) {
        Resource resource =  resourceRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Resource Not Found"));
        return Response.<ResourceResponse>builder()
                .data(toResponse(resource))
                .httpStatusCode(200)
                .message("Resource fetched Successfully with ID: " + id)
                .build();
    }

    @Transactional
    public Response<?> createOrUpdateResource(Long resourceId, String name, ResourceType type, Long courseId, MultipartFile file, String youtubeUrl) throws IOException {
        // Get the course and admin details
        Courses course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        User admin = jwtUtil.getAuthenticatedUserFromContext();
        jwtUtil.ensureAdmin();
        Resource resource;

        // If resource exists, update it; otherwise, create a new one
        if (resourceId != null) {
            resource = resourceRepository.findById(resourceId).orElse(new Resource());
        } else {
            resource = new Resource();
        }

        resource.setName(name);
        resource.setType(type);
        resource.setCourses(course);
        resource.setUploadedBy(admin);

        if (!(ResourceType.VIDEO == type) && file != null) {
            String filePath = saveFile(file, name, type, course);
            resource.setFilePath(filePath);
        } else if (ResourceType.VIDEO == type && youtubeUrl != null) {
            resource.setYoutubeUrl(youtubeUrl);
        }
        Resource res = resourceRepository.save(resource);
        String message = (resourceId!=null?"Resource updated successfully!":"Resource created successfully");
        return Response.builder().data(toResponse(res)).httpStatusCode(200).message(message).build();
    }

    @Transactional
    private String saveFile(MultipartFile file, String name,ResourceType type, Courses course) throws IOException {
        if (course.getDepartments() == null || course.getDepartments().getCollege() == null) {
            throw new IllegalStateException("Course or Department or College is not properly set.");
        }
        College college = course.getDepartments().getCollege();  // Assuming College is related to Department, and Department is related to Course
        Departments department = course.getDepartments();
        String sanitizedCollegeCode = college.getCollegeCode().replaceAll("[^a-zA-Z0-9]", "_");
        String sanitizedDepartmentCode = department.getBranchCode().replaceAll("[^a-zA-Z0-9]", "_");
        String sanitizedCourseName = course.getName().replaceAll("[^a-zA-Z0-9]", "_");
        String sanitizedTypeName = type.name().replaceAll("[^a-zA-Z0-9]", "_");

        Path path = Paths.get(uploadDir, sanitizedCollegeCode, sanitizedDepartmentCode, sanitizedCourseName, sanitizedTypeName);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // Save the file
        String filename = name+"-"+file.getOriginalFilename();
        Path filePath = path.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        return uploadDir + "/" + sanitizedCollegeCode + "/" + sanitizedDepartmentCode + "/" + sanitizedCourseName + "/" + sanitizedTypeName + "/" + filename;
    }

    @Transactional
    public Response<?> deleteResource(Long id) throws Exception {
        jwtUtil.ensureAdmin(); // Ensure the user has admin privileges

        // Check if the resource exists
        Resource resource = resourceRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Resource Not Found!"));

        if (!(resource.getType()==ResourceType.VIDEO)) {
            // Get the resource and potentially delete the file associated with it

            // If there is an associated file, delete it
            try {
                String filePath = resource.getFilePath(); // Assuming the resource has a method to get the file path
                if (filePath != null && !filePath.isEmpty()) {
                    deleteFile(filePath); // Use the deleteFile method discussed earlier
                }
            } catch (IOException e) {
                // Log the error or handle it as appropriate (e.g., continue even if the file deletion fails)
                throw new Exception("Error deleting the associated file for resource ID: " + id, e);
            }

            // Delete the resource itself
            resourceRepository.deleteById(id);

            // Return successful response
            return Response.builder()
                    .httpStatusCode(HttpStatus.NO_CONTENT.value())
                    .message("Resource Deleted Successfully!")
                    .build();
        } else {
            // Resource not found, return NOT_FOUND status
            return Response.builder()
                    .httpStatusCode(HttpStatus.NOT_FOUND.value())
                    .message("Resource not found!")
                    .build();
        }
    }
    private void deleteFile(String filePath) throws IOException {
        // Ensure the file exists before trying to delete it
        Path fileToDelete = Paths.get(filePath);

        if (Files.exists(fileToDelete)) {
            // Delete the file
            Files.delete(fileToDelete);
            System.out.println("File deleted: " + fileToDelete.toString());
        } else {
            throw new RuntimeException("File not found!");
        }

        // Optional: Delete empty directories after file deletion
        Path parentDir = fileToDelete.getParent();
        deleteEmptyDirectories(parentDir);
    }

    private void deleteEmptyDirectories(Path path) throws IOException {
        while (Files.exists(path) && Files.isDirectory(path) && isDirectoryEmpty(path)) {
            Files.delete(path);
            System.out.println("Deleted empty directory: " + path.toString());
            path = path.getParent();  // Move up the directory structure
        }
    }

    private boolean isDirectoryEmpty(Path path) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            return !stream.iterator().hasNext();  // If the directory is empty
        }
    }


    private ResourceResponse toResponse(Resource resource){

        return ResourceResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .type(resource.getType())
                .courseName(resource.getCourses().getName())
                .pathUrl(resource.getFilePath()==null? resource.getYoutubeUrl(): resource.getFilePath())
                .uploadedBy(resource.getUploadedBy().getUsername())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }
}