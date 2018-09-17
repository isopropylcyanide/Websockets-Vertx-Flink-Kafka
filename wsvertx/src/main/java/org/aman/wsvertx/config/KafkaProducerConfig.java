package org.aman.wsvertx.config;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaWriteStream;
import io.vertx.kafka.client.serialization.JsonObjectSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class KafkaProducerConfig {

	public static KafkaWriteStream<String, JsonObject> getKafkaProducerConfig(Vertx vertx) {
		Properties config = new Properties();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonObjectSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonObjectSerializer.class);
		config.put(ProducerConfig.ACKS_CONFIG, "1");

		return KafkaWriteStream.create(vertx, config, String.class, JsonObject.class);
	}
}
