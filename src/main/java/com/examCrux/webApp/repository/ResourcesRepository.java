package com.examCrux.webApp.repository;

import com.examCrux.webApp.entities.Courses;
import com.examCrux.webApp.entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourcesRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByCourses(Courses courses);
}
