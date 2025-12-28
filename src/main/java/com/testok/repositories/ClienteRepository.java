package com.testok.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testok.entities.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findAllByCreatedBy_Username(String username);

    Optional<Cliente> findByIdAndCreatedBy_Username(Long id, String username);

    boolean existsByIdAndCreatedBy_Username(Long id, String username);
}