package org.aman.wsvertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.KafkaWriteStream;
import org.aman.wsvertx.config.KafkaProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import java.util.Optional;

public class KafkaProducerVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(KafkaProducerVerticle.class);

	private String topic;
	private KafkaWriteStream<String, JsonObject> kafkaProducer;

	public KafkaProducerVerticle(String topic) {
		this.topic = topic;
		this.kafkaProducer = null;
	}

	@Override
	public void start(Future<Void> startFuture) {
		logger.info("Deployed verticle that sends to Kafka Topic[" + topic + "]");

		// Listen to the events on the bus with the address "kafka.queue.publisher"
		vertx.eventBus().consumer("kafka.queue.publisher", message -> {
			logger.info(this.topic + " received message: " + message);
			this.kafkaProducer = KafkaProducerConfig.getKafkaProducerConfig(vertx);

			Optional<JsonObject> validJsonRequestOpt = getJsonRequest(message);
			Optional<ProducerRecord<String, JsonObject>> kafkaProducerRecordOpt =
					validJsonRequestOpt.map(jsonReq -> KafkaProducerRecord.create(this.topic, jsonReq))
							.map(KafkaProducerRecord::record);

			kafkaProducerRecordOpt.ifPresent(record -> {
				kafkaProducer.write(record, done -> {
					if (done.succeeded()) {
						RecordMetadata recordMetadata = done.result();
						System.out.println("Message " + record.value() + " written on topic=" + recordMetadata.topic() +
								", partition=" + recordMetadata.partition() +
								", offset=" + recordMetadata.offset());
						message.reply("Published to Kafka");
					}
				});
			});
		}).completionHandler(voidAsyncResult -> {
			if (voidAsyncResult.succeeded()){
				logger.info("kafka.queue.publisher handler set up successful");
				// Signal to the caller that handler setup successfully
				startFuture.complete();
			} else {
				// Signal to the caller that the consumer handler failed
				logger.info("kafka.queue.publisher handler set up failed");
				startFuture.fail(voidAsyncResult.cause());
			}
		});
	}

	/**
	 * Generate a sample json login request
	 */
	private Optional<JsonObject> getJsonRequest(Message<Object> object) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.writeValueAsString(object.body());
			return Optional.ofNullable(new JsonObject(jsonString));
		} catch (Exception e) {
			logger.error("Cannot serialize [" + object + "] into JSON");
		}
		return Optional.empty();
	}
}