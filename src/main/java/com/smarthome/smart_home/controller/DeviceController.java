package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.dto.DeviceStatusUpdateDTO;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.enums.DeviceStatus;
import com.smarthome.smart_home.enums.DeviceType;
import com.smarthome.smart_home.service.DeviceService;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/devices")

public class DeviceController {
    private final DeviceService deviceService;
    public DeviceController(DeviceService deviceService){
        this.deviceService = deviceService;
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices(@RequestParam(required = false) Long roomId, @RequestParam(required = false) DeviceType type, @RequestParam(required = false) DeviceStatus status){
        if (roomId !=null || type != null || status != null) {
            return ResponseEntity.ok(deviceService.getDevicesByFilters(roomId, type, status));
        }
        else {
            return ResponseEntity.ok(deviceService.getAllDevices());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDeviceStatus(@PathVariable Long id, @RequestBody DeviceStatusUpdateDTO updateDTO) {
        return ResponseEntity.ok(deviceService.updateDeviceStatus(id, updateDTO));
    }

    // @GetMapping("/room/{roomId}")
    // public ResponseEntity<List<Device>> getDevicesByRoom(@PathVariable Long roomId) {
    //     return ResponseEntity.ok(deviceService.getDevicesByRoomId(roomId));
    // }
    
    
    
    
   
    
    
}
