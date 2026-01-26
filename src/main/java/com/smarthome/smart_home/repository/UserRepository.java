package com.smarthome.smart_home.repository;

import com.smarthome.smart_home.enums.Role;
import com.smarthome.smart_home.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
        "(:username IS NULL OR LOWER(u.username) LIKE :username) AND " +
        "(:email IS NULL OR LOWER(u.email) LIKE :email) AND " +
        "(:role IS NULL OR :role MEMBER OF u.roles)"
    )
    Page<User> findByFilters(
        @Param("username") String username,
        @Param("email") String email,
        @Param("role") Role role,
        Pageable pageable);
}