package com.smarthome.smart_home.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smarthome.smart_home.dto.DeviceStatusUpdateDTO;
import com.smarthome.smart_home.model.Device;
import com.smarthome.smart_home.model.Device.DeviceStatus;
import com.smarthome.smart_home.repository.DeviceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    
    public List<Device> getAllDevices(){
        return deviceRepository.findAll();
    }

    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }

    public Device updateDeviceStatus(Long id, DeviceStatusUpdateDTO updateDTO){
        Device device = getDeviceById(id);
        device.setStatus(updateDTO.getStatus());
        return deviceRepository.save(device);

    }
}
