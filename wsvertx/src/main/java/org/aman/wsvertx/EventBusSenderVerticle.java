package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import org.aman.wsvertx.model.codec.SampleRequestCodec;
import org.aman.wsvertx.model.payload.ApiRequest;
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
		ApiRequest apiRequest = getApiRequest();
		Thread.sleep(1000);

		DeliveryOptions deliveryOptions = new DeliveryOptions();
		deliveryOptions.setCodecName(SampleRequestCodec.class.getName());
		vertx.eventBus()
				.registerCodec(new SampleRequestCodec())
				.send("kafka.queue.publisher", apiRequest, deliveryOptions, messageAsyncResult -> {
					if (messageAsyncResult.succeeded()){
						logger.info("Message status [" + messageAsyncResult.result().body() + "]");
					}
				});
	}

	/**
	 * Generates a sample api request
	 */
	private ApiRequest getApiRequest() {
		String id = Util.generateRandomUUID();
		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setSenderId(id);
		apiRequest.setEndPoint("/api/users/profile?id=" + id);
		apiRequest.setPayLoad("This is coming from vert-x");
		return apiRequest;
	}
}