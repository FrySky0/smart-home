package com.smarthome.smart_home.dto.update.patch;

import java.time.LocalTime;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import com.smarthome.smart_home.dto.AutomationRuleValidatable;
import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AutomationRulePatchDTO implements AutomationRuleValidatable {
    @Schema(description = "Название правила автоматизации", example = "Включить свет при движении")
    private String name;
    @Schema(description = "Описание правила автоматизации", example = "Это правило включает свет в комнате при обнаружении движения датчиком.")
    private String description;
    @Schema(description = "Флаг, указывающий, включено ли правило автоматизации", example = "true")
    private Boolean enabled;
    @Schema(description = "UUID устройства, которое будет задействовано правилом автоматизации", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID deviceUuid;
    @Schema(description = "UUID датчика, который будет триггером для правила автоматизации", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID sensorUuid;
    @Schema(description = "Событие-триггер для правила автоматизации", example = "GREATER_THAN")
    private TriggerEvent triggerEvent;
    @Schema(description = "Значение сенсора-триггера для правила автоматизации", example = "25.5")
    private Double triggerValue;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Schema(description = "Время-триггер для правила автоматизации", example = "14:30:00")
    private LocalTime triggerTime;
    @Schema(description = "Действие, которое будет выполнено правилом автоматизации", example = "TURN_ON")
    private Action action;
    @Schema(description = "Значение действия для правила автоматизации (если применимо)", example = "75.0")
    private Double actionValue;

}
