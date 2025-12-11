package com.testedti.dronedelivery.models.dtos.response;

import java.time.LocalDateTime;

public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String erro,
    String mensagem
) {}
