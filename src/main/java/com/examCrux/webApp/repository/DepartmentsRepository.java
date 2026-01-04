package com.examCrux.webApp.repository;

import com.examCrux.webApp.entities.College;
import com.examCrux.webApp.entities.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentsRepository extends JpaRepository<Departments, Long> {
    List<Departments> findByCollege(College college);
}
