package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.model.User;
import com.smarthome.smart_home.service.ReportService;
import com.smarthome.smart_home.service.TelegramService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name="Reports", description="Генерация отчетов")
public class ReportController {
    private final ReportService reportService;
    private final TelegramService telegramService;

    @Operation(summary = "Скачать PDF-отчет по истории событий")
    @GetMapping("/activity")
    @PreAuthorize("hasAuthority('systemreport:download')")
    public ResponseEntity<byte[]> downloadActivityReport(@AuthenticationPrincipal User user) {
        telegramService.sendLog("User '" + user.getUsername() + "' generated a PDF activity report");

        byte[] reportContent = reportService.generateActivityReportPDF();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=activity_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportContent);
    }
    
}
