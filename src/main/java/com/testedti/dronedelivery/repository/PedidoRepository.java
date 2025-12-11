package com.testedti.dronedelivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testedti.dronedelivery.models.entity.Pedido;
import java.util.List;
import com.testedti.dronedelivery.models.enums.PrioridadePedido;


public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByPrioridade(PrioridadePedido prioridade);
}
