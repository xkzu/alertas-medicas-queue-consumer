package com.alertasmedicas.app.queue_consumer.dto;

import java.time.LocalDateTime;

public record MeasurementDTO(
        Long id,
        Long idPatient,
        Long idSing,
        double measurementValue,
        LocalDateTime dateTime
) {}
