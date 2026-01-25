package com.smarthome.smart_home.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;
import com.smarthome.smart_home.model.AutomationRule;

@Repository
public interface AutomationRepository extends JpaRepository<AutomationRule, Long> {
        // List<AutomationRule> findAllRules();
        List<AutomationRule> findByEnabledAndSensorId(boolean enabled, Long triggeredSensorId);

        List<AutomationRule> findByEnabled(boolean enabled);

        @Query("SELECT r FROM AutomationRule r " +
           "LEFT JOIN r.device d " +
           "LEFT JOIN r.sensor s " +
           "WHERE (cast(:name as text) IS NULL OR LOWER(r.name) LIKE :name) AND " +
           "(cast(:description as text) IS NULL OR LOWER(r.description) LIKE :description) AND " +
           "(:enabled IS NULL OR r.enabled = :enabled) AND " +
           "(cast(:triggerEvent as text) IS NULL OR r.triggerEvent = :triggerEvent) AND " +
           "(:triggerValue IS NULL OR r.triggerValue = :triggerValue) AND " + // Поменял местами
           "(cast(:triggerTime as text) IS NULL OR r.triggerTime = :triggerTime) AND " + // Поменял местами
           "(cast(:deviceUuid as text) IS NULL OR d.uuid = :deviceUuid) AND " +
           "(cast(:sensorUuid as text) IS NULL OR s.uuid = :sensorUuid) AND " +
           "(cast(:action as text) IS NULL OR r.action = :action) AND " +
           "(:actionValue IS NULL OR r.actionValue = :actionValue)")
        Page<AutomationRule> findByFilters(
                        @Param("name") String name,
                        @Param("description") String description,
                        @Param("enabled") Boolean enabled,
                        @Param("triggerEvent") TriggerEvent triggerEvent,
                        @Param("triggerValue") Double triggerValue,
                        @Param("triggerTime") LocalTime triggerTime,
                        @Param("deviceUuid") UUID deviceUuid,
                        @Param("sensorUuid") UUID sensorUuid,
                        @Param("action") Action action,
                        @Param("actionValue") Double actionValue,
                        Pageable pageable);

        @Query("SELECT r FROM AutomationRule r WHERE r.enabled = true " +
                "AND r.triggerEvent = 'TIME' " +
                "AND r.triggerTime = :currentTime")
        List<AutomationRule> findActiveTimeRules(@Param("currentTime") LocalTime currentTime);
}
