package org.aman.wsvertx.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaWriteStream;
import org.aman.wsvertx.model.payload.RegisterRequest;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaProducerConfig {

	public static KafkaWriteStream<String, RegisterRequest> getKafkaProducerConfig(Vertx vertx) {
		Properties config = new Properties();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		config.put(ProducerConfig.ACKS_CONFIG, "1");
		config.put("schema.registry.url", "http://127.0.0.1:8082");

		return KafkaWriteStream.create(vertx, config);
	}
}
