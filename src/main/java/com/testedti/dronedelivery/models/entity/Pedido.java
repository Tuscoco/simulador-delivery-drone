package com.testedti.dronedelivery.models.entity;

import java.time.LocalDateTime;

import com.testedti.dronedelivery.models.dtos.request.CriarPedidoRequestDTO;
import com.testedti.dronedelivery.models.enums.PrioridadePedido;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_pedidos")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private double pesoPacote;
    private PrioridadePedido prioridade;
    private String descricao;
    private LocalDateTime dataPedido;

    @Embedded
    private Coordenada coordenadaDestino;

    @ManyToOne
    @JoinColumn(name = "entrega_id")
    private Entrega entrega;

    public Pedido(CriarPedidoRequestDTO requestDTO){
        this.pesoPacote = requestDTO.pesoPacote();
        this.prioridade = requestDTO.prioridade();
        this.descricao = requestDTO.descricao();
        this.dataPedido = LocalDateTime.now();
        this.coordenadaDestino = new Coordenada(
            requestDTO.coordenadaDestino().x(),
            requestDTO.coordenadaDestino().y()
        );
    }

}
