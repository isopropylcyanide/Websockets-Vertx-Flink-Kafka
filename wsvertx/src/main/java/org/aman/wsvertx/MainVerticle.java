package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.apache.log4j.Logger;

public class MainVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(MainVerticle.class);

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		logger.info("Deployed main module " + startFuture + Thread.currentThread().getName());
		vertx.deployVerticle(new EventBusSenderVerticle());
	}

	@Override
	public void stop() throws Exception {
		logger.info("Successfully stopping verticle.");
	}

	public static void main(String[] args) throws InterruptedException {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MainVerticle());
	}
}
