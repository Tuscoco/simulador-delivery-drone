package com.testedti.dronedelivery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.testedti.dronedelivery.models.entity.Drone;
import com.testedti.dronedelivery.models.enums.EstadoDrone;


public interface DroneRepository extends JpaRepository<Drone, Long> {
    List<Drone> findByEstado(EstadoDrone estado);
    
    @Query("SELECT d FROM Drone d WHERE d.capacidadeCarga >= :capacidade")
    List<Drone> findByCapacidadeGreaterThanOrEqual(@Param("capacidade") double capacidade);
}
