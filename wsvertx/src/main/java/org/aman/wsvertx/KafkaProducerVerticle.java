package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.apache.log4j.Logger;

public class KafkaProducerVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(KafkaProducerVerticle.class);

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		logger.info("Successfully started verticle" + startFuture);
	}

	@Override
	public void stop() throws Exception {
		logger.info("Successfully stopping verticle");
	}

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new KafkaProducerVerticle());
	}
}
