package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.apache.log4j.Logger;

/**
 * Receives an event from the event bus.
 */
public class EventBusKafkaSenderVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(EventBusKafkaSenderVerticle.class);

	public void start(Future<Void> startFuture) throws InterruptedException {
		logger.info("Deployed verticle [" + this.getClass().getName() + "]");

		// Deploy the kafka producer verticle that reads events on "kafka.queue.publisher"
		vertx.deployVerticle(new KafkaProducerVerticle("flink-demo"), stringAsyncResult -> {
			if (stringAsyncResult.succeeded()) {
				// Once the kafka producer handler is setup successfully, send periodic requests
				logger.info("Kafka producer handler setup successful");
			}
		});
	}

}