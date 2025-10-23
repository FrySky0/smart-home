package com.smarthome.smart_home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.dto.CreateRoomDTO;
import com.smarthome.smart_home.dto.RoomDTO;
import com.smarthome.smart_home.mappers.RoomMapper;
import com.smarthome.smart_home.model.Room;
import com.smarthome.smart_home.service.RoomService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Rooms", description = "Управление комнатами")
public class RoomController {
    private final RoomService roomService;
    private final RoomMapper roomMapper;

    public RoomController(RoomService roomService, RoomMapper roomMapper) {
        this.roomService = roomService;
        this.roomMapper = roomMapper;
    }

    // Получить все комнаты
    @GetMapping()
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> roomDTOs = roomService.getAllRooms().stream()
                .map(roomMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roomDTOs);
    }

    // Получить комнату по ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable @NotNull Long id) {
        Room room = roomService.getRoomById(id);
        if (room != null) {
            return ResponseEntity.ok(roomMapper.toDTO(room));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Поиск комнат по этажу или имени
    // Пример: /api/rooms/search?floor=2
    @GetMapping("/search")
    public ResponseEntity<List<RoomDTO>> searchRooms(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) String name) {
        List<RoomDTO> roomDTOs = new ArrayList<>();
        if (floor != null) {
            roomDTOs = roomService.getRoomsByFloor(floor).stream()
                    .map(roomMapper::toDTO)
                    .collect(Collectors.toList());
        } else if (name != null && !name.isEmpty()) {
            roomDTOs = roomService.findRoomsByName(name).stream()
                    .map(roomMapper::toDTO)
                    .collect(Collectors.toList());
        } else if (id != null) {
            roomDTOs.add(roomMapper.toDTO(roomService.getRoomById(id)));
        } else {
            roomDTOs = roomService.getAllRooms().stream()
                    .map(roomMapper::toDTO)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(roomDTOs);
    }

    // Создать новую комнату
    @PostMapping()
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody CreateRoomDTO createRoomDTO) {
        Room room = roomMapper.toEntity(createRoomDTO);
        Room savedRoom = roomService.createRoom(room);
        return ResponseEntity.ok(roomMapper.toDTO(savedRoom));
    }

    // Обновить комнату
    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable @NotNull Long id,
            @Valid @RequestBody CreateRoomDTO createRoomDTO) {
        Room room = roomMapper.toEntity(createRoomDTO);
        Room updatedRoom = roomService.updateRoom(id, room);
        return ResponseEntity.ok(roomMapper.toDTO(updatedRoom));
    }

    // Удалить комнату
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable @NotNull Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
