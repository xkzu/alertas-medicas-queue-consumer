package com.alertasmedicas.app.queue_consumer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private  String queueAnomaly;

    @Value("${rabbitmq.queue.name2}")
    private String queueVitalsSigns;

    @Bean
    public Queue queueAnomaly() {
        return new Queue(queueAnomaly, true);
    }

    @Bean
    public Queue queueVitalsSigns() {
        return new Queue(queueVitalsSigns, true);
    }
}
