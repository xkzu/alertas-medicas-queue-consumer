package com.alertasmedicas.app.queue_consumer.util;

import com.alertasmedicas.app.queue_consumer.dto.MeasurementDTO;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {

    private MessageParser() {}

    public static MeasurementDTO parseMeasurement(String text) {
        // Expresión regular para extraer los valores
        Pattern pattern = Pattern.compile("id=(\\w+), idPatient=(\\d+), idSing=(\\d+), measurementValue=([\\d.]+), dateTime=([\\d\\-T:.]+)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            Long id = matcher.group(1).equals("null") ? null : Long.parseLong(matcher.group(1));
            Long idPatient = Long.parseLong(matcher.group(2));
            Long idSing = Long.parseLong(matcher.group(3));
            double measurementValue = Double.parseDouble(matcher.group(4));
            LocalDateTime dateTime = LocalDateTime.parse(matcher.group(5));

            return new MeasurementDTO(id, idPatient, idSing, measurementValue, dateTime);
        }

        throw new IllegalArgumentException("Formato inválido de MeasurementDTO: " + text);
    }

}
