package com.alertasmedicas.app.queue_consumer.util;

import com.alertasmedicas.app.queue_consumer.dto.FakerDTO;
import com.alertasmedicas.app.queue_consumer.dto.MeasurementDTO;
import com.alertasmedicas.app.queue_consumer.dto.PatientDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class JsonFileWriter {

    private JsonFileWriter() {}

    public static void saveJsonToFile(String rawText, String filePath) {
        try {
            List<FakerDTO> fakerDataList = parseFakerDTOFromText(rawText);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Soporte para LocalDateTime
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Formato bonito

            objectMapper.writeValue(new File(filePath), fakerDataList);

            log.info("✅ Archivo JSON guardado exitosamente en: {}", filePath);
        } catch (IOException e) {
            log.error("❌ Error al guardar el archivo JSON. {}", e.getMessage());
        }
    }

    private static List<FakerDTO> parseFakerDTOFromText(String rawText) {
        List<FakerDTO> fakerList = new ArrayList<>();

        Pattern pattern = Pattern.compile(
                "FakerDTO\\[patient=PatientDTO\\[id=(\\d+), idDoctor=(\\d+), name=([^,]+), state=([^\\]]+)\\], measurements=\\[(.*?)\\]\\]"
        );

        Matcher matcher = pattern.matcher(rawText);

        while (matcher.find()) {
            Long idPatient = Long.parseLong(matcher.group(1));
            Long idDoctor = Long.parseLong(matcher.group(2));
            String name = matcher.group(3);
            String state = matcher.group(4);
            String measurementsText = matcher.group(5);

            List<MeasurementDTO> measurements = extractMeasurements(measurementsText, idPatient);

            fakerList.add(new FakerDTO(new PatientDTO(idPatient, idDoctor, name, state), measurements));
        }

        return fakerList;
    }

    private static List<MeasurementDTO> extractMeasurements(String measurementsText, Long idPatient) {
        List<MeasurementDTO> measurementList = new ArrayList<>();

        Pattern pattern = Pattern.compile(
                "MeasurementDTO\\[id=null, idPatient=\\d+, idSing=(\\d+), measurementValue=([0-9.]+), dateTime=([0-9:T.-]+)]"
        );

        Matcher matcher = pattern.matcher(measurementsText);

        while (matcher.find()) {
            Long idSing = Long.parseLong(matcher.group(1));
            double measurementValue = Double.parseDouble(matcher.group(2));
            LocalDateTime dateTime = LocalDateTime.parse(matcher.group(3));

            measurementList.add(new MeasurementDTO(null, idPatient, idSing, measurementValue, dateTime));
        }

        return measurementList;
    }
}
