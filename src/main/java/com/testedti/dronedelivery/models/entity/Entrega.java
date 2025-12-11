package com.testedti.dronedelivery.models.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.testedti.dronedelivery.models.enums.StatusEntrega;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_entregas")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Entrega {
    
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY) 
    private Long id;

    private double distanciaTotal;
    private double tempoEstimado;
    private StatusEntrega status;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraConclusao;

    @OneToOne
    private Drone drone;

    @OneToMany(mappedBy = "entrega")
    private List<Pedido> pedidos;

}
