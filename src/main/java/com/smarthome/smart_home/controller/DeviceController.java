package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.dto.DeviceStatusUpdateDTO;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.service.DeviceService;

import java.util.List;

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
    public ResponseEntity<List<Device>> getAllDevices(){
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDeviceStatus(@PathVariable Long id, @RequestBody DeviceStatusUpdateDTO updateDTO) {
        return ResponseEntity.ok(deviceService.updateDeviceStatus(id, updateDTO));
    }
    
   
    
    
}
