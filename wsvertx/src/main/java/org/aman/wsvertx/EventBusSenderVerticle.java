package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.aman.wsvertx.model.payload.SampleRequest;
import org.aman.wsvertx.util.Util;
import org.apache.log4j.Logger;

/**
 * Receives an event from the event bus.
 */
public class EventBusSenderVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(EventBusSenderVerticle.class);

	public void start(Future<Void> startFuture) throws InterruptedException {
		logger.info("Deployed event sender verticle");
		vertx.deployVerticle(new ReceiverKafkaProducerVerticle("topic1"));
		SampleRequest sampleRequest = getSampleRequest();
		Thread.sleep(1000);
		vertx.eventBus().publish("kafka.queue.publisher", sampleRequest.toString());
	}

	private SampleRequest getSampleRequest() {
		SampleRequest sampleRequest = new SampleRequest();
		sampleRequest.setData("This is coming from vert-x");
		sampleRequest.setSenderId(Util.generateRandomUUID());
		return sampleRequest;
	}
}