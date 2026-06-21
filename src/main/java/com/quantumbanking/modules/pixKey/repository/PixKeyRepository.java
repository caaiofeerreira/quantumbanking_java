package com.quantumbanking.modules.pixKey.repository;

import com.quantumbanking.modules.pixKey.domain.PixKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PixKeyRepository extends JpaRepository<PixKey, UUID> {

    boolean existsByKey(String key);

    long countByAccountId(Long accountId);

    Optional<PixKey> findByKey(String key);

}