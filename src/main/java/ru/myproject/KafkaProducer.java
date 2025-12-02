package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaProducer {

    private static final String TOPIC_NAME = "test-topic";
    private static final String BOOTSTRAP_SERVERS = "rc1a-b4vq5pcf2hdbp40r.mdb.yandexcloud.net:9091";
    private static final String USERNAME = "kafka-test-user";
    private static final String PASSWORD = "kafka-test-pass";

    private final Producer<String, String> producer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRUSTSTORE_LOCATION = "C:/Users/abaev/.kafka/CA.pem";
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "SCRAM-SHA-512");
        props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.scram.ScramLoginModule required " +
                        "username=\"" + USERNAME + "\" " +
                        "password=\"" + PASSWORD + "\";");
        props.put("ssl.truststore.location", TRUSTSTORE_LOCATION);
        props.put("ssl.truststore.type", "PEM");

        this.producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
    }

    public void sendMessage(UserMessage message) throws Exception {
        String jsonMessage = objectMapper.writeValueAsString(message);

        ProducerRecord<String, String> record =
                new ProducerRecord<>(TOPIC_NAME, message.getId(), jsonMessage);

        try {
            RecordMetadata metadata = producer.send(record).get();
            LOGGER.info("Message sent successfully to partition {} with offset {}", metadata.partition(), metadata.offset());
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Error sending message: {}", e.getMessage());
            throw e;
        }
    }

    public void sendMessageAsync(UserMessage message) throws Exception {
        String jsonMessage = objectMapper.writeValueAsString(message);

        ProducerRecord<String, String> record =
                new ProducerRecord<>(TOPIC_NAME, message.getId(), jsonMessage);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                LOGGER.error("Error sending message: {}", exception.getMessage());
            } else {
                LOGGER.info("Message sent to partition {} with offset {}", metadata.partition(), metadata.offset());
            }
        });
    }

    public void close() {
        producer.close();
    }

    public static void main(String[] args) {
        KafkaProducer producer = new KafkaProducer();

        try {
            // Отправка тестовых сообщений
            UserMessage message1 = new UserMessage("1", "John Doe");
            UserMessage message2 = new UserMessage("2", "Jane Smith");
            UserMessage message3 = new UserMessage("3", "Bob Johnson");

            producer.sendMessage(message1);
            producer.sendMessageAsync(message2);
            producer.sendMessageAsync(message3);

            Thread.sleep(1000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}