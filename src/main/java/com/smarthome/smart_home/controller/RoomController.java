package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.model.Room;
import com.smarthome.smart_home.service.DeviceService;
import com.smarthome.smart_home.service.RoomService;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;
    public RoomController(RoomService roomService){
        this.roomService = roomService;
    }

    @GetMapping()
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Room room = roomService.getRoomById(id);
        if (room != null) {
            return ResponseEntity.ok(room);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam(required = false) Integer floor, @RequestParam(required = false) String name) {

        if(floor != null){
            return ResponseEntity.ok(roomService.getRoomsByFloor(floor));
        }
        else if (name != null && !name.isEmpty()){
            return ResponseEntity.ok(roomService.findRoomsByName(name));
        }
        return ResponseEntity.ok(roomService.getAllRooms());
    }
    
    @PostMapping()
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        
        return ResponseEntity.ok(roomService.createRoom(room));
    }
    
    
}
