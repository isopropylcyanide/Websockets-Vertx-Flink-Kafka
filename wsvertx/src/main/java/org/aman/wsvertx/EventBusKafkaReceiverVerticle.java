package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.apache.log4j.Logger;

/**
 * Receives an event from the event bus.
 */
public class EventBusKafkaReceiverVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(EventBusKafkaReceiverVerticle.class);

	public void start(Future<Void> startFuture) throws InterruptedException {
		logger.info("Deployed event kafka receiver sender verticle");

		// Deploy the kafka consumer verticle that reads incoming messages on topic fink-demo-resp
		vertx.deployVerticle(new KafkaConsumerVerticle("flink-demo-resp"), stringAsyncResult -> {
			if (stringAsyncResult.succeeded()) {
				logger.info("Kafka consumer verticle executed successfully");
			}
		});
	}
}