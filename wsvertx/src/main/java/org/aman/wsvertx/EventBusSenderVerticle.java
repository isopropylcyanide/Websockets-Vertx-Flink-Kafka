package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import org.aman.wsvertx.model.codec.SampleRequestCodec;
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
		DeliveryOptions deliveryOptions = new DeliveryOptions();
		deliveryOptions.setCodecName(SampleRequestCodec.class.getName());
		vertx.eventBus()
				.registerCodec(new SampleRequestCodec())
				.send("kafka.queue.publisher", sampleRequest, deliveryOptions, messageAsyncResult -> {
					if (messageAsyncResult.succeeded()){
						logger.info("Message status [" + messageAsyncResult.result().body() + "]");
					}
				});
	}

	private SampleRequest getSampleRequest() {
		SampleRequest sampleRequest = new SampleRequest();
		sampleRequest.setData("This is coming from vert-x");
		sampleRequest.setSenderId(Util.generateRandomUUID());
		return sampleRequest;
	}
}