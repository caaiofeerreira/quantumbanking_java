package com.quantumbanking.modules.bank.repository;

import com.quantumbanking.modules.bank.domain.manager.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    @Query("SELECT m FROM Manager m WHERE m.id = :userId")
    Optional<Manager> findByUserId(@Param("userId") Long userId);
}