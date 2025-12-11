package com.testedti.dronedelivery.models.dtos.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.testedti.dronedelivery.models.entity.Entrega;
import com.testedti.dronedelivery.models.enums.StatusEntrega;

public record EntregaResponseDTO(
    Long id,
    double distanciaTotal,
    double tempoEstimado,
    StatusEntrega status,
    LocalDateTime dataHoraInicio,
    LocalDateTime dataHoraConclusao,
    Long droneId,
    String droneModelo,
    List<Long> pedidoIds
) {
    public EntregaResponseDTO(Entrega entrega) {
        this(
            entrega.getId(),
            entrega.getDistanciaTotal(),
            entrega.getTempoEstimado(),
            entrega.getStatus(),
            entrega.getDataHoraInicio(),
            entrega.getDataHoraConclusao(),
            entrega.getDrone() != null ? entrega.getDrone().getId() : null,
            entrega.getDrone() != null ? entrega.getDrone().getModelo() : null,
            entrega.getPedidos() != null ? 
                entrega.getPedidos().stream()
                    .map(p -> p.getId())
                    .collect(Collectors.toList()) : null
        );
    }
}
