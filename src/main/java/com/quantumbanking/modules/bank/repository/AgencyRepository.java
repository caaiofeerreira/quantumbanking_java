package com.quantumbanking.modules.bank.repository;

import com.quantumbanking.modules.bank.domain.agency.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Long> {


    @Query("SELECT a FROM Agency a WHERE a.number = :number")
    Optional<Agency> findByNumber(@Param("number") String number);


    @Query("SELECT a.number AS number, a.address.cidade AS cidade FROM Agency a")
    List<String> findAllAgencyNumbersAndCidade();
}

