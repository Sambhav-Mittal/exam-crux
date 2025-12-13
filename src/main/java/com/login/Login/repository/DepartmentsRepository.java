package com.login.Login.repository;

import com.login.Login.entities.College;
import com.login.Login.entities.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentsRepository extends JpaRepository<Departments, Long> {
    List<Departments> findByCollege(College college);
}
