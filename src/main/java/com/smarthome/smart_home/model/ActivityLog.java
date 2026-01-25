package com.smarthome.smart_home.model;

import java.time.LocalDateTime;

import com.smarthome.smart_home.enums.activitylog.ComponentName;
import com.smarthome.smart_home.enums.activitylog.LogAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
public class ActivityLog {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private ComponentName componentName;
    @Enumerated(EnumType.STRING)
    private LogAction action;
    @Column(columnDefinition = "TEXT")
    private String details;

    public ActivityLog(ComponentName componentName, LogAction action, String details){
        this.componentName = componentName;
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
