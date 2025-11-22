package com.smarthome.smart_home.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smarthome.smart_home.enums.SensorType;
import com.smarthome.smart_home.model.Sensor;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    List<Sensor> findByRoomId(Long roomId);

    List<Sensor> findByType(SensorType type);

    List<Sensor> findByTypeAndValueGreaterThan(SensorType type, Double value);

    @Query("SELECT s FROM Sensor s WHERE " +
            "(:roomId IS NULL OR s.room.id = :roomId) AND " +
            "(:type IS NULL OR s.type = :type)")
    List<Sensor> findByFilters(
            @Param("roomId") Long roomId,
            @Param("type") SensorType type);

    @Query("SELECT s FROM Sensor s WHERE " +
            "(:roomId IS NULL OR s.room.id = :roomId) AND " +
            "(:type IS NULL OR s.type = :type)")
    Page<Sensor> findByFilters(
            @Param("roomId") Long roomId,
            @Param("type") SensorType type,
            Pageable pageable);

    @Query("SELECT s FROM Sensor s WHERE s.room.floor = :floor")
    List<Sensor> findByFloor(@Param("floor") Integer floor);
}
