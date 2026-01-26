package com.smarthome.smart_home.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.smarthome.smart_home.enums.activitylog.ComponentName;
import com.smarthome.smart_home.enums.activitylog.LogAction;
import com.smarthome.smart_home.model.User;
import com.smarthome.smart_home.service.FileService;
import com.smarthome.smart_home.service.LogService;
import com.smarthome.smart_home.service.TelegramService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Tag(name = "Configuration Import", description = "Загрузка конфигурации из файла")
public class ConfigFileController {
    private final FileService fileService;
    private final TelegramService telegramService;
    private final LogService logService;
    @Operation(summary= "Импорт комнат и устройств из JSON файла")
    @PreAuthorize("hasAuthority('configuration:import')")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importConfig(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal User user){
        telegramService.sendLog("User '" + user.getUsername() + "' started configuration import");
        if (file.isEmpty()){
            return ResponseEntity.badRequest().body("File is empty");
        }
        fileService.importConfiguration(file);
        logService.log(ComponentName.Configuration, LogAction.IMPORTED, "");
        return ResponseEntity.ok(Map.of(
            "message", "Configuration imported successfully",
            "fileName", file.getOriginalFilename()
        ));
    }

    @Operation(summary = "Экспорт всей конфигурации системы в JSON (Отчет)")
    @PreAuthorize("hasAuthority('configuration:export')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportConfig(@AuthenticationPrincipal User user) {
        telegramService.sendLog("User '" + user.getUsername() + "' started configuration export");
        byte[] data = fileService.exportConfigurationJSON();
        logService.log(ComponentName.Configuration, LogAction.EXPORTED, "");
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=smart_home_report.json")
            .contentType(MediaType.APPLICATION_JSON)
            .body(data);
    }
    
    

}
