package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class ClientSocketRequestVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> future) throws Exception {

		vertx.eventBus().consumer("ws-handler", message -> {
			//Consume web requests intercepted by this handler

			//Create handler for unique id to send the response back



		});
	}

	@Override
	public void stop(Future<Void> future) throws Exception {

	}
}
