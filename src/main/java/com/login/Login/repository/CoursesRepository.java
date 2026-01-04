package com.login.Login.repository;

import com.login.Login.entities.Courses;
import com.login.Login.entities.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursesRepository extends JpaRepository<Courses, Long> {
    List<Courses> findByDepartmentsAndSem(Departments departments, String sem);
    List<Courses> findByDepartments(Departments departments);

}
