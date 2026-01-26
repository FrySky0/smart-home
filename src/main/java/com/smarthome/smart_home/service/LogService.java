package com.smarthome.smart_home.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.smart_home.enums.activitylog.ComponentName;
import com.smarthome.smart_home.enums.activitylog.LogAction;
import com.smarthome.smart_home.model.ActivityLog;
import com.smarthome.smart_home.repository.ActivityLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogService {
    private final ActivityLogRepository logRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void log(ComponentName componentName, LogAction action, String details) {
        logRepository.save(new ActivityLog(componentName, action, details));
    }

    @Transactional
    public void logEntity(ComponentName componentName, LogAction action, Object dto){
        String jsonDetails = "Error serializing details";
        try {
            jsonDetails = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize DTO", e);
        }
        logRepository.save(new ActivityLog(componentName, action, jsonDetails));
    }

    public List<ActivityLog> getLastEvents(int limit) {
        return logRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, limit));
    }
}