package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.apache.log4j.Logger;

public class MainVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(MainVerticle.class);

	public static void main(String[] args) throws InterruptedException {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MainVerticle());
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		logger.info("Deployed main module " + startFuture + Thread.currentThread().getName());

		//Deploy the server verticle that listens to socket reqs with unique id
		vertx.deployVerticle(new ServerSocketEventBusVerticle());

		//Deploy the client verticle that sends response to socket with unique id
		vertx.deployVerticle(new ClientSocketRequestVerticle());

		//Deploy the kafka sender verticle
		vertx.deployVerticle(new EventBusKafkaSenderVerticle());

		//Deploy the kafka receiver verticle
		vertx.deployVerticle(new EventBusKafkaReceiverVerticle());
	}

	@Override
	public void stop() throws Exception {
		logger.info("Successfully stopping verticle.");
	}
}
