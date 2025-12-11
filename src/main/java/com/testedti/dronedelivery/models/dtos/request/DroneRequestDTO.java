package com.testedti.dronedelivery.models.dtos.request;

public record DroneRequestDTO(
    String modelo,
    double capacidadeCarga,
    double autonomiaVoo
) {}