package com.smarthome.smart_home.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.smart_home.dto.importexport.RoomImportDTO;
import com.smarthome.smart_home.enums.DeviceStatus;
import com.smarthome.smart_home.enums.DeviceType;
import com.smarthome.smart_home.enums.SensorType;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.model.Room;
import com.smarthome.smart_home.model.Sensor;
import com.smarthome.smart_home.repository.RoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void importConfiguration(MultipartFile file){
        log.info("Starting configuration import from file: {}", file.getOriginalFilename());

        try {
            List<RoomImportDTO> importData = objectMapper.readValue(
                file.getInputStream(), new TypeReference<List<RoomImportDTO>>() {});
            
            for (RoomImportDTO roomDto : importData){
                Room room = new Room();
                room.setName(roomDto.getName());
                room.setFloor(roomDto.getFloor());
                // Room savedRoom = roomRepository.save(room);
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
        } catch (IOException e) {
            log.error("Failed to read import file", e);
            throw new RuntimeException("Error reading file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Invalid Enum type in JSON", e);
            throw new RuntimeException("Invalid device or sensor type in JSON");
        }
    }

    public byte[] exportConfigurationJSON(){
        try {
            List<Room> rooms = roomRepository.findAll();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(rooms);
        } catch (IOException e) {
            log.error("Failed to export configuration", e);
            throw new RuntimeException("Error during export: " + e.getMessage());
        }
    }
}
