package com.smarthome.smart_home.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smarthome.smart_home.model.Room;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByFloor(Integer floor);

    Page<Room> findByFloor(Integer floor, Pageable pageable);

    Page<Room> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Room> findByNameContainingIgnoreCase(String name);

    @Query("SELECT r FROM Room r WHERE r.floor BETWEEN :minFloor AND :maxFloor")
    List<Room> findByFloorRange(@Param("minFloor") Integer minFloor, @Param("maxFloor") Integer maxFloor);
}
