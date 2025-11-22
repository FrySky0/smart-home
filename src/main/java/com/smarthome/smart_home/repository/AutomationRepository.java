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
import com.smarthome.smart_home.enums.automation.Action;
import com.smarthome.smart_home.enums.automation.TriggerEvent;
import com.smarthome.smart_home.model.AutomationRule;
import com.smarthome.smart_home.model.Device;

@Repository
public interface AutomationRepository extends JpaRepository<AutomationRule, Long> {
        // List<AutomationRule> findAllRules();
        List<AutomationRule> findByEnabledAndTriggerSensorId(boolean enabled, Long triggeredSensorId);

        List<AutomationRule> findByEnabled(boolean enabled);

        @Query("SELECT r FROM AutomationRule r WHERE " +
                        "(:enabled IS NULL OR r.enabled = :enabled) AND " +
                        "(:triggerEvent IS NULL OR r.triggerEvent = :triggerEvent) AND " +
                        "(:triggerValue IS NULL OR r.triggerValue = :triggerValue) AND " +
                        "(:trigger_device_id IS NULL OR r.triggerDevice.id = :trigger_device_id) AND " +
                        "(:trigger_sensor_id IS NULL OR r.triggerSensor.id = :trigger_sensor_id) AND " +
                        "(:action IS NULL OR r.action = :action) AND" +
                        "(:actionValue IS NULL OR r.actionValue = :actionValue)")
        List<AutomationRule> findByFilters(
                        @Param("enabled") Boolean enabled,
                        @Param("triggerEvent") TriggerEvent triggerEvent,
                        @Param("triggerValue") Integer triggerValue,
                        @Param("trigger_device_id") Long triggerDeviceId,
                        @Param("trigger_sensor_id") Long triggerSensorId,
                        @Param("action") Action action,
                        @Param("actionValue") Double actionValue);

        @Query("SELECT r FROM AutomationRule r WHERE " +
                        "(:enabled IS NULL OR r.enabled = :enabled) AND " +
                        "(:triggerEvent IS NULL OR r.triggerEvent = :triggerEvent) AND " +
                        "(:triggerValue IS NULL OR r.triggerValue = :triggerValue) AND " +
                        "(:trigger_device_id IS NULL OR r.triggerDevice.id = :trigger_device_id) AND " +
                        "(:trigger_sensor_id IS NULL OR r.triggerSensor.id = :trigger_sensor_id) AND " +
                        "(:action IS NULL OR r.action = :action) AND" +
                        "(:actionValue IS NULL OR r.actionValue = :actionValue)")
        Page<AutomationRule> findByFilters(
                        @Param("enabled") Boolean enabled,
                        @Param("triggerEvent") TriggerEvent triggerEvent,
                        @Param("triggerValue") Integer triggerValue,
                        @Param("trigger_device_id") Long triggerDeviceId,
                        @Param("trigger_sensor_id") Long triggerSensorId,
                        @Param("action") Action action,
                        @Param("actionValue") Double actionValue,
                        Pageable pageable);
}
