package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaReadStream;
import org.aman.wsvertx.config.KafkaConsumerConfig;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Optional;

public class KafkaConsumerVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(KafkaConsumerVerticle.class);

	private String topic;
	private KafkaReadStream<String, JsonObject> kafkaConsumer;

	public KafkaConsumerVerticle(String topic) {
		this.topic = topic;
		this.kafkaConsumer = null;
	}

	@Override
	public void start(Future<Void> startFuture) {
		logger.info("Deployed verticle [" + this.getClass().getName() + "] topic[" + this.topic + "]");

		//Create the kafka consumer
		kafkaConsumer = KafkaConsumerConfig.getKafkaConsumerConfig(vertx);

		// Subscribe to the correct kafka topic
		kafkaConsumer.subscribe(Collections.singleton(this.topic));

		//Attach the consumer handler
		kafkaConsumer.handler(record -> {
			// Extract the message & put it back into event.bus for ws handler to intercept
			JsonObject message = record.value();
			logger.info("Consumed message from Kafka topic [" + this.topic + "]: " + message);

			//Extract the sender id from the message
			Optional<String> senderId = Optional.ofNullable(message.getValue("senderId"))
					.map(String::valueOf);

			//Put the event back to the websocket handler address on the bus
			senderId.ifPresent(id -> {
				vertx.eventBus().send("ws-handler-" + id, message, messageAsyncResult -> {
					if (messageAsyncResult.succeeded()) {
						logger.info("Kafka consumer event handled successfully [ws-handler-" + id + "]");
					} else {
						logger.info("Failed to send event to [ws-handler-" + id + "]");
					}
				});
			});
		});

		// Handle errors in the kafka consumer
		kafkaConsumer.exceptionHandler(exception -> {
			logger.error("Error while receiving message from Kafka " + exception.getMessage(), exception);
			startFuture.fail(exception);
		});
	}

	@Override
	public void stop() throws Exception {
		if (null != kafkaConsumer) {
			kafkaConsumer.unsubscribe(voidAsyncResult -> {
				if (voidAsyncResult.succeeded()) {
					logger.info("Consumer successfully unsubscribed");
				}
			});
			kafkaConsumer.close(voidAsyncResult -> {
				if (voidAsyncResult.succeeded()) {
					logger.info("Consumer [" + this.topic + "] closed successfully");
				} else {
					logger.info("Consumer [" + this.topic + "] failed to close");
				}
			});
		}
	}
}