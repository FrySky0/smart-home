package com.smarthome.smart_home.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smarthome.smart_home.dto.create.RoomCreateDTO;
import com.smarthome.smart_home.dto.response.RoomResponseDTO;
import com.smarthome.smart_home.dto.update.patch.RoomPatchDTO;
import com.smarthome.smart_home.dto.update.put.RoomPutDTO;
import com.smarthome.smart_home.mappers.RoomMapper;
import com.smarthome.smart_home.model.Room;
import com.smarthome.smart_home.service.RoomService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

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

    @GetMapping()
    public ResponseEntity<Page<RoomResponseDTO>> getAllRooms(
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        log.info("Getting all rooms with filters - floor: {}, name: {}, page: {}, size: {}, sortBy: {}, sortDirection: {}",
                floor, name, page, size, sortBy, sortDirection);

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RoomResponseDTO> roomPage = roomService.getRoomsByFilters(floor,name,pageable)
                .map(roomMapper::toDTO);

        log.info("Successfully retrieved {} rooms", roomPage.getNumberOfElements());
        return ResponseEntity.ok(roomPage);
    }

    // Получить комнату по ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getRoomById(@PathVariable @NotNull Long id) {
        log.info("Getting room by ID: {}", id);

        Room room = roomService.getRoomById(id);
        log.info("Room found with ID: {}", id);
        return ResponseEntity.ok(roomMapper.toDTO(room));
    }

    // Создать новую комнату
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponseDTO> createRoom(@Valid @RequestBody RoomCreateDTO createRoomDTO) {
        log.info("Creating new room with name: {}", createRoomDTO.getName());

        Room room = roomMapper.toEntity(createRoomDTO);
        Room savedRoom = roomService.createRoom(room);

        log.info("Room created successfully with ID: {}", savedRoom.getId());
        return ResponseEntity.ok(roomMapper.toDTO(savedRoom));
    }

    // Обновить комнату
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponseDTO> updateRoomFull(@PathVariable @NotNull Long id,
            @Valid @RequestBody RoomPutDTO roomPutDTO) {
        log.info("Updating room with ID: {}", id);

        Room updatedRoom = roomService.updateFull(id, roomPutDTO);

        log.info("Room updated successfully with ID: {}", id);
        return ResponseEntity.ok(roomMapper.toDTO(updatedRoom));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponseDTO> updateRoomPartially(@PathVariable @NotNull Long id,
            @Valid @RequestBody RoomPatchDTO roomPatchDTO) {
        log.info("Updating room with ID: {}", id);

        Room updatedRoom = roomService.updatePartially(id, roomPatchDTO);

        log.info("Room updated successfully with ID: {}", id);
        return ResponseEntity.ok(roomMapper.toDTO(updatedRoom));
    }
    // Удалить комнату
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable @NotNull Long id) {
        log.info("Deleting room with ID: {}", id);

        roomService.deleteRoom(id);

        log.info("Room deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
