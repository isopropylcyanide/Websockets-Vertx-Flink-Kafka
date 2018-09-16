package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import org.aman.wsvertx.model.codec.LoginRequestCodec;
import org.aman.wsvertx.model.payload.LoginRequest;
import org.aman.wsvertx.util.Util;
import org.apache.log4j.Logger;

/**
 * Receives an event from the event bus.
 */
public class EventBusSenderVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(EventBusSenderVerticle.class);

	public void start(Future<Void> startFuture) throws InterruptedException {
		logger.info("Deployed event sender verticle");
		vertx.deployVerticle(new ReceiverKafkaProducerVerticle("flink-demo"));
		LoginRequest loginRequest = getLoginRequest();
		Thread.sleep(1000);

		DeliveryOptions deliveryOptions = new DeliveryOptions();
		deliveryOptions.setCodecName(LoginRequestCodec.class.getName());
		vertx.eventBus()
				.registerCodec(new LoginRequestCodec())
				.send("kafka.queue.publisher", loginRequest, deliveryOptions, messageAsyncResult -> {
					if (messageAsyncResult.succeeded()){
						logger.info("Message status [" + messageAsyncResult.result().body() + "]");
					}
				});
	}

	/**
	 * Generates a sample api request
	 */
	private LoginRequest getLoginRequest() {
		String id = Util.generateRandomUUID();
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUsername("user");
		loginRequest.setPassword("password");
		loginRequest.setSenderId(id);
		loginRequest.setRequestUrl("/api/login?id=" + loginRequest.getSenderId());
		return loginRequest;
	}
}