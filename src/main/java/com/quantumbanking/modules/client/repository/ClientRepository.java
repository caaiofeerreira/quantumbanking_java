package com.quantumbanking.modules.client.repository;

import com.quantumbanking.modules.client.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
