package com.quantumbanking.modules.shared.repository;

import com.quantumbanking.modules.shared.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface  UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByCpf(@Param("cpf")String cpf);

    boolean existsByCpf(String cpf);
}