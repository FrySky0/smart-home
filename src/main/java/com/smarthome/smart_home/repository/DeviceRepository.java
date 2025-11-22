package com.smarthome.smart_home.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smarthome.smart_home.enums.DeviceStatus;
import com.smarthome.smart_home.enums.DeviceType;
import com.smarthome.smart_home.model.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findByRoomId(Long roomId);

    List<Device> findByType(DeviceType type);

    List<Device> findByStatus(DeviceStatus status);

    @Query("SELECT d FROM Device d WHERE " +
            "(:roomId IS NULL OR d.room.id = :roomId) AND " +
            "(:type IS NULL OR d.type = :type) AND " +
            "(:status IS NULL OR d.status = :status)")
    List<Device> findByFilters(
            @Param("roomId") Long roomId,
            @Param("type") DeviceType type,
            @Param("status") DeviceStatus status);

    @Query("SELECT d FROM Device d WHERE " +
            "(:roomId IS NULL OR d.room.id = :roomId) AND " +
            "(:type IS NULL OR d.type = :type) AND " +
            "(:status IS NULL OR d.status = :status)")
    Page<Device> findByFilters(
            @Param("roomId") Long roomId,
            @Param("type") DeviceType type,
            @Param("status") DeviceStatus status,
            Pageable pageable);

    @Query("SELECT d FROM Device d WHERE d.room.name = :roomName")
    List<Device> findByRoomName(@Param("roomName") String roomName);
}
