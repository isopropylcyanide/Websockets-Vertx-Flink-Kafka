package org.aman.wsvertx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;
import org.aman.wsvertx.config.KafkaProducerConfig;
import org.aman.wsvertx.model.payload.LoginRequest;
import org.apache.log4j.Logger;

import java.util.Optional;

public class ReceiverKafkaProducerVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(ReceiverKafkaProducerVerticle.class);

	private String topic;
	private KafkaProducer<String, String> kafkaProducer;

	public ReceiverKafkaProducerVerticle(String topic) {
		this.topic = topic;
		this.kafkaProducer = null;
	}

	public void start(Future<Void> startFuture) {
		logger.info("Deployed verticle that sends to Kafka Topic[" + topic + "]");

		vertx.eventBus().consumer("kafka.queue.publisher", message -> {
			LoginRequest loginRequest = (LoginRequest) message.body();
			logger.info(this.topic + " received message: " + loginRequest);
			this.kafkaProducer = KafkaProducerConfig.getKafkaProducerConfig(vertx);

			Optional<String> validJsonRequest = getJsonLoginRequest(loginRequest);
			Optional<KafkaProducerRecord<String, String>> kafkaProducerRecordOpt =
					validJsonRequest.map(jsonReq -> KafkaProducerRecord.create(this.topic, jsonReq));

			kafkaProducerRecordOpt.ifPresent(record -> {
				kafkaProducer.write(record, done -> {
					if (done.succeeded()) {
						RecordMetadata recordMetadata = done.result();
						System.out.println("Message " + record.value() + " written on topic=" + recordMetadata.getTopic() +
								", partition=" + recordMetadata.getPartition() +
								", offset=" + recordMetadata.getOffset());
						message.reply("Published to Kafka");
					}
				});
			});
		}).completionHandler(voidAsyncResult -> {
			if (voidAsyncResult.succeeded()){
				logger.info("kafka.queue.publisher handler set up successful");
			}
			else{
				logger.info("kafka.queue.publisher handler set up failed");
			}
		});
	}

	private Optional<String> getJsonLoginRequest(LoginRequest loginRequest) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return Optional.ofNullable(mapper.writeValueAsString(loginRequest));
		} catch (JsonProcessingException e) {
			logger.error("Cannot serialize [" + loginRequest + "] into JSON");
		}
		return Optional.empty();
	}
}