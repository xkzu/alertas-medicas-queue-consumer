package com.alertasmedicas.app.queue_consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import java.nio.charset.StandardCharsets;

@Log4j2
@SpringBootApplication
public class QueueConsumerApplication implements CommandLineRunner {

	@Value("${rabbitmq.queue.name}")
	private String queueName;

	@Value("${rabbitmq.host}")
	private String host;

	public static void main(String[] args) {
		org.springframework.boot.SpringApplication.run(QueueConsumerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Establece conexión y canal a RabbitMQ
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		// Declara la cola de la cual consumir con la propiedad durable
		boolean durable = true;

		// Declara la cola de la cual consumir
		channel.queueDeclare(queueName, durable, false, false, null);
		log.info("Esperando mensaje encolado...");

		// Define la función de callback para el consumidor
		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
			log.info("Mensaje recibido: {}", message);
		};
		// Consume mensajes de la cola
		channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
		});
	}
}
