package com.testedti.dronedelivery.models.dtos.request;

import com.testedti.dronedelivery.models.enums.PrioridadePedido;

public record CriarPedidoRequestDTO(
    double pesoPacote,
    CoordenadaDTO coordenadaDestino,
    PrioridadePedido prioridade,
    String descricao
) {}
