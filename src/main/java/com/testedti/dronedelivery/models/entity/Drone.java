package com.testedti.dronedelivery.models.entity;

import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.testedti.dronedelivery.models.dtos.request.DroneRequestDTO;
import com.testedti.dronedelivery.models.enums.EstadoDrone;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_drones")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Drone {
    
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String modelo;

    private double capacidadeCarga;
    private double autonomiaVoo;
    private double autonomiaAtual;
    private EstadoDrone estado;

    @Embedded
    private Coordenada coordenadaAtual;

    @OneToOne(mappedBy = "drone")
    @JsonIgnore
    private Entrega entregaAtiva;

    public Drone(DroneRequestDTO dto){
        this.modelo = dto.modelo();
        this.capacidadeCarga = dto.capacidadeCarga();
        this.autonomiaVoo = dto.autonomiaVoo();
        this.autonomiaAtual = dto.autonomiaVoo();
        this.estado = EstadoDrone.DISPONIVEL;
        this.coordenadaAtual = new Coordenada(0, 0);
    }

}
