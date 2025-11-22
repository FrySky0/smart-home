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
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Slf4j
public class RoomController {
    private final RoomService roomService;
    private final RoomMapper roomMapper;

    public RoomController(RoomService roomService, RoomMapper roomMapper) {
        this.roomService = roomService;
        this.roomMapper = roomMapper;
    }

    // Получить все комнаты
    @GetMapping()
    public ResponseEntity<Page<RoomDTO>> getAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        log.info("Getting all rooms - page: {}, size: {}, sortBy: {}, sortDirection: {}",
                page, size, sortBy, sortDirection);

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RoomDTO> roomPage = roomService.getAllRooms(pageable)
                .map(roomMapper::toDTO);

        log.info("Successfully retrieved {} rooms", roomPage.getNumberOfElements());
        return ResponseEntity.ok(roomPage);
    }

    // Получить комнату по ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable @NotNull Long id) {
        log.info("Getting room by ID: {}", id);

        Room room = roomService.getRoomById(id);
        if (room != null) {
            log.info("Room found with ID: {}", id);
            return ResponseEntity.ok(roomMapper.toDTO(room));
        } else {
            log.warn("Room not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    // Поиск комнат по этажу или имени
    // Пример: /api/rooms/search?floor=2
    @GetMapping("/search")
    public ResponseEntity<Page<RoomDTO>> searchRooms(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        log.info("Searching rooms - id: {}, floor: {}, name: {}, page: {}, size: {}, sortBy: {}, sortDirection: {}",
                id, floor, name, page, size, sortBy, sortDirection);

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RoomDTO> roomPage;
        if (floor != null) {
            log.debug("Searching by floor: {}", floor);
            roomPage = roomService.getRoomsByFloor(floor, pageable)
                    .map(roomMapper::toDTO);
        } else if (name != null && !name.isEmpty()) {
            log.debug("Searching by name: {}", name);
            roomPage = roomService.findRoomsByName(name, pageable)
                    .map(roomMapper::toDTO);
        } else if (id != null) {
            log.debug("Searching by ID: {}", id);
            Room room = roomService.getRoomById(id);
            Page<RoomDTO> singlePage = Page.empty(pageable);
            if (room != null) {
                singlePage = new org.springframework.data.domain.PageImpl<>(
                        List.of(roomMapper.toDTO(room)), pageable, 1);
            }
            roomPage = singlePage;
        } else {
            log.debug("No specific search criteria provided, returning all rooms");
            roomPage = roomService.getAllRooms(pageable)
                    .map(roomMapper::toDTO);
        }

        log.info("Search completed, found {} rooms", roomPage.getNumberOfElements());
        return ResponseEntity.ok(roomPage);
    }

    // Создать новую комнату
    @PostMapping()
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody CreateRoomDTO createRoomDTO) {
        log.info("Creating new room with name: {}", createRoomDTO.getName());

        Room room = roomMapper.toEntity(createRoomDTO);
        Room savedRoom = roomService.createRoom(room);

        log.info("Room created successfully with ID: {}", savedRoom.getId());
        return ResponseEntity.ok(roomMapper.toDTO(savedRoom));
    }

    // Обновить комнату
    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable @NotNull Long id,
            @Valid @RequestBody CreateRoomDTO createRoomDTO) {
        log.info("Updating room with ID: {}", id);

        Room room = roomMapper.toEntity(createRoomDTO);
        Room updatedRoom = roomService.updateRoom(id, room);

        log.info("Room updated successfully with ID: {}", id);
        return ResponseEntity.ok(roomMapper.toDTO(updatedRoom));
    }

    // Удалить комнату
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable @NotNull Long id) {
        log.info("Deleting room with ID: {}", id);

        roomService.deleteRoom(id);

        log.info("Room deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
