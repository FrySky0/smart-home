package com.smarthome.smart_home.dto.importexport;

import java.util.List;

import lombok.Data;

@Data
public class SmartHomeConfigDTO {
    private List<RoomImportDTO> rooms;
    private List<AutomationRuleImportDTO> automationRules;
}
