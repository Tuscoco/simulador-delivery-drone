package com.testedti.dronedelivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testedti.dronedelivery.models.entity.Entrega;

public interface EntregaRepository extends JpaRepository<Entrega, Long> {
    
}
