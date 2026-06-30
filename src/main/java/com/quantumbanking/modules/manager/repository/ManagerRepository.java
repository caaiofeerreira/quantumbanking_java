package com.quantumbanking.modules.manager.repository;

import com.quantumbanking.modules.manager.domain.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    @Query("SELECT m FROM Manager m WHERE m.id = :userId")
    Optional<Manager> findByUserId(@Param("userId") Long userId);

    @Query("SELECT m.agencyId FROM Manager m WHERE m.id = :userId")
    Optional<Long> findAgencyIdByUserId(@Param("userId") Long userId);

    List<Manager> findAllByAgencyId(Long agencyId);
}