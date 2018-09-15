package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.apache.log4j.Logger;

public class ReceiverKafkaProducerVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(ReceiverKafkaProducerVerticle.class);

	private String topic;

	public ReceiverKafkaProducerVerticle(String topic) {
		this.topic = topic;
	}

	public void start(Future<Void> startFuture) {
		logger.info("Deployed verticle that sends to Kafka Topic[" + topic + "]");
		vertx.eventBus().consumer("kafka.queue.publisher", message -> {
			logger.info(message);
			logger.info(this.topic + " received message: " + message.body());
		});
	}
}