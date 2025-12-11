package com.testedti.dronedelivery.models.dtos.request;

import java.util.List;

public record CriarEntregaRequestDTO(
    List<Long> pedidoIds
) {}
