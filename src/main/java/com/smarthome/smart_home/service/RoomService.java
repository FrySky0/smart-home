package com.smarthome.smart_home.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.smarthome.smart_home.exception.ResourceNotFoundException;
import com.smarthome.smart_home.model.Room;
import com.smarthome.smart_home.repository.RoomRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        log.info("Fetching all rooms");
        List<Room> rooms = roomRepository.findAll();
        log.debug("Retrieved {} rooms", rooms.size());
        return rooms;
    }

    public Page<Room> getAllRooms(Pageable pageable) {
        log.info("Fetching all rooms with pagination - page: {}, size: {}", pageable.getPageNumber(),
                pageable.getPageSize());
        Page<Room> rooms = roomRepository.findAll(pageable);
        log.debug("Retrieved {} rooms out of {} total", rooms.getNumberOfElements(), rooms.getTotalElements());
        return rooms;
    }

    public Room getRoomById(Long id) {
        log.info("Fetching room by id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Room not found with id: {}", id);
                    return new ResourceNotFoundException("Room not found with id: " + id);
                });
        log.debug("Successfully retrieved room: {} (id: {})", room.getName(), room.getId());
        return room;
    }

    public List<Room> getRoomsByFloor(Integer floor) {
        log.info("Fetching rooms by floor: {}", floor);
        if (floor == null || floor < 0) {
            log.warn("Invalid floor value provided: {}", floor);
            throw new ValidationException("Floor must be a positive number");
        }
        List<Room> rooms = roomRepository.findByFloor(floor);
        log.debug("Retrieved {} rooms on floor {}", rooms.size(), floor);
        return rooms;
    }

    public Page<Room> getRoomsByFloor(Integer floor, Pageable pageable) {
        log.info("Fetching rooms by floor: {} with pagination - page: {}, size: {}", floor, pageable.getPageNumber(),
                pageable.getPageSize());
        if (floor == null || floor < 0) {
            log.warn("Invalid floor value provided: {}", floor);
            throw new ValidationException("Floor must be a positive number");
        }
        Page<Room> rooms = roomRepository.findByFloor(floor, pageable);
        log.debug("Retrieved {} rooms on floor {} out of {} total", rooms.getNumberOfElements(), floor,
                rooms.getTotalElements());
        return rooms;
    }

    public List<Room> findRoomsByName(String name) {
        log.info("Searching rooms by name: '{}'", name);
        if (name == null || name.isEmpty()) {
            log.warn("Empty room name provided for search");
            throw new ValidationException("Room name is required");
        }
        List<Room> rooms = roomRepository.findByNameContainingIgnoreCase(name);
        log.debug("Found {} rooms matching name '{}'", rooms.size(), name);
        return rooms;
    }

    public Page<Room> findRoomsByName(String name, Pageable pageable) {
        log.info("Searching rooms by name: '{}' with pagination - page: {}, size: {}", name, pageable.getPageNumber(),
                pageable.getPageSize());
        if (name == null || name.isEmpty()) {
            log.warn("Empty room name provided for search");
            throw new ValidationException("Room name is required");
        }
        Page<Room> rooms = roomRepository.findByNameContainingIgnoreCase(name, pageable);
        log.debug("Found {} rooms matching name '{}' out of {} total", rooms.getNumberOfElements(), name,
                rooms.getTotalElements());
        return rooms;
    }

    public Room createRoom(Room room) {
        log.info("Creating new room: {}", room.getName());
        if (room.getName() == null || room.getName().isEmpty()) {
            log.warn("Attempt to create room with empty name");
            throw new ValidationException("Room name is required");
        }
        if (room.getFloor() == null || room.getFloor() < 0) {
            log.warn("Attempt to create room with invalid floor: {}", room.getFloor());
            throw new ValidationException("Floor must be a positive number");
        }
        Room savedRoom = roomRepository.save(room);
        log.info("Successfully created room: {} (id: {})", savedRoom.getName(), savedRoom.getId());
        return savedRoom;
    }

    public Room updateRoom(Long id, Room roomDetails) {
        log.info("Updating room with id: {}", id);
        Room existingRoom = getRoomById(id);

        if (roomDetails.getName() == null || roomDetails.getName().isEmpty()) {
            log.warn("Attempt to update room {} with empty name", id);
            throw new ValidationException("Room name is required");
        }
        if (roomDetails.getFloor() == null || roomDetails.getFloor() < 0) {
            log.warn("Attempt to update room {} with invalid floor: {}", id, roomDetails.getFloor());
            throw new ValidationException("Floor must be a positive number");
        }

        log.debug("Updating room {}: name from '{}' to '{}', floor from {} to {}",
                id, existingRoom.getName(), roomDetails.getName(),
                existingRoom.getFloor(), roomDetails.getFloor());

        existingRoom.setName(roomDetails.getName());
        existingRoom.setFloor(roomDetails.getFloor());
        Room updatedRoom = roomRepository.save(existingRoom);
        log.info("Successfully updated room: {} (id: {})", updatedRoom.getName(), updatedRoom.getId());
        return updatedRoom;
    }

    public void deleteRoom(Long id) {
        log.info("Deleting room with id: {}", id);
        if (!roomRepository.existsById(id)) {
            log.error("Attempt to delete non-existent room with id: {}", id);
            throw new ResourceNotFoundException("Room not found with id: " + id);
        }
        roomRepository.deleteById(id);
        log.info("Successfully deleted room with id: {}", id);
    }
}
