package com.alertasmedicas.app.queue_consumer.dto;

public record PatientDTO (
        Long id,
        Long idDoctor,
        String name,
        String state
) {}
