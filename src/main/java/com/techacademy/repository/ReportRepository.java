package com.techacademy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Query("SELECT r FROM Report r WHERE r.employee.code = :employeeCode")
    List<Report> findReportsByEmployeeCode(@Param("employeeCode") String employeeCode);
}