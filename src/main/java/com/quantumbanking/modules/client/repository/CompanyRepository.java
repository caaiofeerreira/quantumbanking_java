package com.quantumbanking.modules.client.repository;

import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByClient(Client client);

}