package com.smarthome.smart_home.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smarthome.smart_home.model.Room;
import java.util.List;


public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByFloor(Integer floor);

    List<Room> findByNameContainingIgnoreCase(String name);

    @Query("SELECT r FROM Room r WHERE r.floor BETWEEN :minFloor AND :maxFloor")
    List<Room> findByFloorRange(@Param("minFloor") Integer minFloor, @Param("maxFloor") Integer maxFloor);
}
