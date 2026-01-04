package com.login.Login.repository;

import com.login.Login.entities.Courses;
import com.login.Login.entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourcesRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByCourses(Courses courses);
}
