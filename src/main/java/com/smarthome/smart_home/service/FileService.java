package com.smarthome.smart_home.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.smart_home.dto.importexport.AutomationRuleImportDTO;
import com.smarthome.smart_home.dto.importexport.DeviceImportDTO;
import com.smarthome.smart_home.dto.importexport.RoomImportDTO;
import com.smarthome.smart_home.dto.importexport.SensorImportDTO;
import com.smarthome.smart_home.dto.importexport.SmartHomeConfigDTO;
import com.smarthome.smart_home.enums.DeviceStatus;
import com.smarthome.smart_home.enums.DeviceType;
import com.smarthome.smart_home.enums.SensorType;
import com.smarthome.smart_home.model.AutomationRule;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.model.Room;
import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.repository.AutomationRepository;
import com.smarthome.smart_home.repository.DeviceRepository;
import com.smarthome.smart_home.repository.RoomRepository;
import com.smarthome.smart_home.repository.SensorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;
    private final DeviceRepository deviceRepository;
    private final SensorRepository sensorRepository;
    private final AutomationRepository automationRepository;

    @Transactional
    public void importConfiguration(MultipartFile file){
        log.info("Starting configuration import from file: {}", file.getOriginalFilename());

        try {
            SmartHomeConfigDTO config = objectMapper.readValue(
                file.getInputStream(), SmartHomeConfigDTO.class);
            
            if (config.getRooms() != null) {
                for (RoomImportDTO roomDto : config.getRooms()){
                    Room room = new Room();
                    room.setName(roomDto.getName());
                    room.setFloor(roomDto.getFloor());
                    log.debug("Imported room: {}", room.getName());

                    if (roomDto.getDevices() != null){
                        for (var deviceDto : roomDto.getDevices()){
                            Device device = new Device();
                            device.setName(deviceDto.getName());
                            device.setUuid(deviceDto.getUuid());
                            device.setType(DeviceType.valueOf(deviceDto.getType()));
                            device.setStatus(DeviceStatus.OFF);
                            device.setRoom(room);
                            room.getDevices().add(device);
                        }
                    }
                    if (roomDto.getSensors() != null){
                        for (var sensorDto : roomDto.getSensors()){
                            Sensor sensor = new Sensor();
                            sensor.setName(sensorDto.getName());
                            sensor.setUuid(sensorDto.getUuid());
                            sensor.setType(SensorType.valueOf(sensorDto.getType()));
                            sensor.setValue(0.0);
                            sensor.setRoom(room);
                            room.getSensors().add(sensor);
                        }
                    }
                    
                    roomRepository.save(room);
                }
            }
            if (config.getAutomationRules() != null) {
                for (AutomationRuleImportDTO ruleDto : config.getAutomationRules()) {
                    AutomationRule rule = new AutomationRule();
                    rule.setName(ruleDto.getName());
                    rule.setDescription(ruleDto.getDescription());
                    rule.setEnabled(ruleDto.isEnabled());
                    rule.setTriggerEvent(ruleDto.getTriggerEvent());
                    rule.setTriggerValue(ruleDto.getTriggerValue());
                    rule.setTriggerTime(ruleDto.getTriggerTime());
                    rule.setAction(ruleDto.getAction());
                    rule.setActionValue(ruleDto.getActionValue());

                    Device device = deviceRepository.findByUuid(ruleDto.getDeviceUuid())
                        .orElseThrow(() -> new RuntimeException("Device not found for UUID: " + ruleDto.getDeviceUuid()));
                    rule.setDevice(device);
                    if (ruleDto.getSensorUuid() != null) {
                        Sensor sensor = sensorRepository.findByUuid(ruleDto.getSensorUuid())
                            .orElseThrow(() -> new RuntimeException("Sensor not found for UUID: " + ruleDto.getSensorUuid()));
                        rule.setSensor(sensor);
                    }
                    automationRepository.save(rule);
                }
            }

        } catch (IOException e) {
            log.error("Failed to read import file", e);
            throw new RuntimeException("Error reading file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Invalid Enum type in JSON", e);
            throw new RuntimeException("Invalid device or sensor type in JSON");
        }
    }


    public byte[] exportConfigurationJSON() {
        log.info("Exporting full configuration to JSON");
        try {
            SmartHomeConfigDTO config = new SmartHomeConfigDTO();


            List<RoomImportDTO> roomDtos = roomRepository.findAll().stream().map(room -> {
                RoomImportDTO rDto = new RoomImportDTO();
                rDto.setName(room.getName());
                rDto.setFloor(room.getFloor());
                
                rDto.setDevices(room.getDevices().stream().map(d -> {
                    DeviceImportDTO dDto = new DeviceImportDTO();
                    dDto.setName(d.getName());
                    dDto.setUuid(d.getUuid());
                    dDto.setType(d.getType().name());
                    return dDto;
                }).collect(Collectors.toList()));

                rDto.setSensors(room.getSensors().stream().map(s -> {
                    SensorImportDTO sDto = new SensorImportDTO();
                    sDto.setName(s.getName());
                    sDto.setUuid(s.getUuid());
                    sDto.setType(s.getType().name());
                    return sDto;
                }).collect(Collectors.toList()));
                
                return rDto;
            }).collect(Collectors.toList());

            List<AutomationRuleImportDTO> ruleDtos = automationRepository.findAll().stream().map(rule -> {
                AutomationRuleImportDTO arDto = new AutomationRuleImportDTO();
                arDto.setName(rule.getName());
                arDto.setDescription(rule.getDescription());
                arDto.setEnabled(rule.isEnabled());
                arDto.setTriggerEvent(rule.getTriggerEvent());
                arDto.setTriggerValue(rule.getTriggerValue());
                arDto.setTriggerTime(rule.getTriggerTime());
                arDto.setAction(rule.getAction());
                arDto.setActionValue(rule.getActionValue());
                
                arDto.setDeviceUuid(rule.getDevice().getUuid());
                if (rule.getSensor() != null) {
                    arDto.setSensorUuid(rule.getSensor().getUuid());
                }
                
                return arDto;
            }).collect(Collectors.toList());

            config.setRooms(roomDtos);
            config.setAutomationRules(ruleDtos);

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(config);
        } catch (IOException e) {
            log.error("Failed to export configuration", e);
            throw new RuntimeException("Error during export: " + e.getMessage());
        }
    }
}
