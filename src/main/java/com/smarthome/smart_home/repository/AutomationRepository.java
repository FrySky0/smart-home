package com.smarthome.smart_home.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smarthome.smart_home.model.AutomationRule;
@Repository
public interface AutomationRepository extends JpaRepository<AutomationRule, Long> {
    // List<AutomationRule> findAllRules();
    List<AutomationRule> findByEnabledAndTriggerSensorId(boolean enabled, Long triggeredSensorId);
    List<AutomationRule> findByEnabled(boolean enabled);
}
