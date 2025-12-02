package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumer {

    private static final String TOPIC_NAME = "test-topic";
    private static final String BOOTSTRAP_SERVERS = "rc1a-b4vq5pcf2hdbp40r.mdb.yandexcloud.net:9091";
    private static final String GROUP_ID = "user-messages-group";
    private static final String USERNAME = "kafka-test-user";
    private static final String PASSWORD = "kafka-test-pass";
    private static final String TRUSTSTORE_LOCATION = "C:/Users/abaev/.kafka/CA.pem"; // Путь к сертификату
    private final Consumer<String, String> consumer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    public KafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "SCRAM-SHA-512");
        props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.scram.ScramLoginModule required " +
                        "username=\"" + USERNAME + "\" " +
                        "password=\"" + PASSWORD + "\";");
        // SSL настройки для Yandex Cloud
        props.put("ssl.truststore.location", TRUSTSTORE_LOCATION);
        props.put("ssl.truststore.type", "PEM");

        // Дополнительные SSL настройки
        props.put("ssl.endpoint.identification.algorithm", "HTTPS");
        this.consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList(TOPIC_NAME));
    }

    public void consumeMessages() {
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {
                    try {
                        UserMessage message = objectMapper.readValue(record.value(), UserMessage.class);
                        LOGGER.info("Received message: {}, Partition: {}, Offset: {}", message, record.partition(), record.offset());

                        processMessage(message);

                    } catch (Exception e) {
                        LOGGER.error("Error parsing message: {}", e.getMessage());
                    }
                }
            }
        } finally {
            consumer.close();
        }
    }

    private void processMessage(UserMessage message) {
        LOGGER.info("Processing message: ID={}, Name={}", message.getId(), message.getName());
    }

    public void close() {
        consumer.close();
    }

    public static void main(String[] args) {
        KafkaConsumer consumer = new KafkaConsumer();
        LOGGER.info("Starting consumer...");
        consumer.consumeMessages();
    }
}