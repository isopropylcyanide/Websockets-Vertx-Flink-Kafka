package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import org.aman.wsvertx.model.payload.LoginRequest;
import org.aman.wsvertx.util.Util;
import org.apache.log4j.Logger;

import java.util.Random;

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

		this.httpClient = vertx.createHttpClient(options).websocket(9443, "localhost", "/wsapi", webSocket -> {
			// Set the handler for processing server response if any
			webSocket.handler(dataBuffer -> {
				logger.info("Received response from server " + dataBuffer);
			});

			// Emulate client side requests
			LoginRequest loginRequest = new LoginRequest();
			loginRequest.setUsername("user" + new Random().nextInt());
			loginRequest.setPassword("password" + new Random().nextInt());
			loginRequest.setRequestUrl("/api/auth/login?username=" + loginRequest.getUsername()
					+ "&password=" + loginRequest.getPassword());
			Util.getJsonStringFromObject(loginRequest)
					.ifPresent(webSocket::writeTextMessage);

		});
	}

	@Override
	public void stop(Future<Void> future) throws Exception {
		if (null != this.httpClient) {
			this.httpClient.close();
		}
	}
}
