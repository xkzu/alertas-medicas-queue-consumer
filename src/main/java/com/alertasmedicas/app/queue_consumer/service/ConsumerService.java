package com.alertasmedicas.app.queue_consumer.service;

import com.alertasmedicas.app.queue_consumer.dto.MeasurementDTO;
import com.alertasmedicas.app.queue_consumer.util.JsonFileWriter;
import com.alertasmedicas.app.queue_consumer.util.MessageParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class ConsumerService {

    private final RestTemplate restTemplate;

    @Value("${file.path}")
    private String filePath;

    @Value("${api.measurement:}")
    private String domain;

    @Autowired
    public ConsumerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RabbitListener(queues = "#{queueAnomaly}")
    public void receiveMessageAnomaly(String message) {
        log.info("Mensaje recibido cola anomaly: {}", message);
        saveAnomaly(message);
    }

    @RabbitListener(queues = "#{queueVitalsSigns}")
    public void receiveMessageVitalsSigns(String message) {
        log.info("Mensaje recibido cola vitals: {}", message);
        JsonFileWriter.saveJsonToFile(message, filePath);
        log.info("Json creado correctamente {}", filePath);
    }

    private void saveAnomaly(String message) {
        log.info("Guardando anomalia en bd: {}", message);
        MeasurementDTO measurementDTO = MessageParser.parseMeasurement(message);
        log.info("Parseando message a measurement: {}", measurementDTO);
        MeasurementDTO measurementSaved = saveMeasurement(measurementDTO);
        log.info("Anomalia guardada: {}", measurementSaved);
    }

    private MeasurementDTO saveMeasurement(MeasurementDTO measurementDTO) {
        String url = domain + "/measurement/add";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MeasurementDTO> requestEntity = new HttpEntity<>(measurementDTO, headers);
        ResponseEntity<MeasurementDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                MeasurementDTO.class
        );
        return response.getBody();
    }
}
