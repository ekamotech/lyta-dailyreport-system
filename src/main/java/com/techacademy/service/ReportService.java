package com.techacademy.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ReportService(ReportRepository reportRepository, PasswordEncoder passwordEncoder) {
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



}
