package com.quantumbanking.modules.client.repository;

import com.quantumbanking.modules.client.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

}