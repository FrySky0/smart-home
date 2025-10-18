package com.smarthome.smart_home.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smarthome.smart_home.exception.ResourceNotFoundException;
import com.smarthome.smart_home.model.Room;
import com.smarthome.smart_home.repository.RoomRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    public List<Room> getRoomsByFloor(Integer floor) {
        if (floor == null || floor < 0) {
            throw new ValidationException("Floor must be a positive number");
        }
        return roomRepository.findByFloor(floor);
    }

    public List<Room> findRoomsByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new ValidationException("Room name is required");
        }
        return roomRepository.findByNameContainingIgnoreCase(name);
    }

    public Room createRoom(Room room) {
        if (room.getName() == null || room.getName().isEmpty()) {
            throw new ValidationException("Room name is required");
        }
        if (room.getFloor() == null || room.getFloor() < 0) {
            throw new ValidationException("Floor must be a positive number");
        }
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room roomDetails) {
        Room existingRoom = getRoomById(id);

        if (roomDetails.getName() == null || roomDetails.getName().isEmpty()) {
            throw new ValidationException("Room name is required");
        }
        if (roomDetails.getFloor() == null || roomDetails.getFloor() < 0) {
            throw new ValidationException("Floor must be a positive number");
        }

        existingRoom.setName(roomDetails.getName());
        existingRoom.setFloor(roomDetails.getFloor());
        return roomRepository.save(existingRoom);
    }

    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room not found with id: " + id);
        }
        roomRepository.deleteById(id);
    }
}
