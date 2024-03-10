package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.service.EmployeeService;

@Service
public class ReportService {

    private final EmployeeService employeeService;
    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ReportService(EmployeeService employeeService, ReportRepository reportRepository, PasswordEncoder passwordEncoder) {
        this.employeeService = employeeService;
        this.reportRepository = reportRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            boolean isAdmin = authorities.stream()
                .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));

            if (!isAdmin) {
                // 一般権限のユーザーの場合
                // System.out.println("ADMIN権限を持っていません");
                // ユーザーが作成した日報のみを取得
                return reportRepository.findReportsByEmployeeCode(userDetails.getUsername());
            } 

        }

        // 管理者権限のユーザーの場合
        // System.out.println("ADMIN権限を持っています");
        return reportRepository.findAll();

    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id, UserDetail userDetail) {

        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

        // ログインユーザーの社員番号を取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String employeeCode = userDetail.getUsername();
        System.out.println(employeeCode);

        // Employeeエンティティを取得
        Employee employee = employeeService.findByCode(employeeCode);

        // EmployeeエンティティをReportエンティティに関連付け
        report.setEmployee(employee);

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }



}
