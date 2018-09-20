package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import org.aman.wsvertx.model.payload.RegisterRequest;
import org.aman.wsvertx.util.Util;
import org.apache.log4j.Logger;

public class ClientSocketRequestVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(ClientSocketRequestVerticle.class);
	private HttpClient httpClient;

	public ClientSocketRequestVerticle() {
		this.httpClient = null;
	}

	@Override
	public void start(Future<Void> future) throws Exception {
		logger.info("Deployed verticle [" + this.getClass().getName());

		HttpClientOptions options = new HttpClientOptions()
				.setSsl(false)
				.setTrustAll(true);

		this.httpClient = vertx.createHttpClient(options).websocket(9443, "localhost", "/wsapi/register",
				webSocket -> {
			// Set the handler for processing server response if any
			webSocket.handler(dataBuffer -> {
				logger.info("Received response from server " + dataBuffer);
			});

			// Emulate client side register request
			RegisterRequest registerRequest = createClientRegisterRequest();
			Util.getJsonStringFromObject(registerRequest)
					.ifPresent(webSocket::writeTextMessage);

		});
	}

	/**
	 * Creates a dummy client register request
	 */
	private RegisterRequest createClientRegisterRequest() {
		RegisterRequest registerRequest = new RegisterRequest();
		registerRequest.setEmail("amangarg1995sep@gmail.com");
		registerRequest.setPassword("test");
		registerRequest.setRegisterAsAdmin(true);
		return registerRequest;
	}

	@Override
	public void stop(Future<Void> future) throws Exception {
		if (null != this.httpClient) {
			this.httpClient.close();
		}
	}
}
