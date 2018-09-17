package org.aman.wsvertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.shareddata.LocalMap;
import org.aman.wsvertx.model.codec.LoginRequestCodec;
import org.aman.wsvertx.model.payload.LoginRequest;
import org.apache.log4j.Logger;

import java.io.IOException;

public class ServerSocketEventBusVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(ServerSocketEventBusVerticle.class);
	private HttpServer httpServer;

	public ServerSocketEventBusVerticle() {
		this.httpServer = null;
	}

	@Override
	public void start(Future<Void> startFuture){
		logger.info("Deployed verticle [" + this.getClass().getName() + "]");
		this.httpServer = vertx.createHttpServer();

		httpServer.websocketHandler(webSocket -> {
			LocalMap<String, String> wsSessions = vertx.sharedData().getLocalMap("ws.sessions");

			//Filter socket base url
			if (webSocket.path().equals("/wsapi")) {
				logger.info("Request received at socket [" + webSocket.textHandlerID() + "]");
				wsSessions.put(webSocket.textHandlerID(), webSocket.textHandlerID());

				// Set delivery options to include a custom codec for sending the login request
				DeliveryOptions deliveryOptions = new DeliveryOptions();
				deliveryOptions.setCodecName(LoginRequestCodec.class.getName());
				vertx.eventBus().registerDefaultCodec(LoginRequest.class, new LoginRequestCodec());

				// Set handler for the incoming text data
				webSocket.textMessageHandler(data -> {
					logger.info("Received web socket data [" + data + "]");
					ObjectMapper mapper = new ObjectMapper();
					try {
						LoginRequest loginRequest = mapper.readValue(data, LoginRequest.class);
						loginRequest.setSenderId(webSocket.textHandlerID());
						logger.info("Sending to kafka topic: data [" + loginRequest + "]");
						// Send raw socket data to kafka producer event bus
						vertx.eventBus()
								.send("ws.messages.producer.event.bus", loginRequest, deliveryOptions, messageAsyncResult -> {
									if (messageAsyncResult.succeeded()) {
										logger.info("Message status [" + messageAsyncResult.result().body() + "]");
									}
								});

						// Receive processed data from kafka consumer and write back to the socket
						vertx.eventBus().consumer("ws-handler-" + webSocket.textHandlerID(), kafkaMessage -> {
							logger.info("Received message from Kafka: " + kafkaMessage.body());
							webSocket.writeTextMessage(kafkaMessage.body().toString());
						});
					} catch (IOException e) {
						logger.error("Error deserializing websocket data [" + data + "] id [" + webSocket.textHandlerID() + "]");
						e.printStackTrace();
					}
				});
			}
			else {
				logger.info("Websocket path [" + webSocket.path() + " is invalid");
				webSocket.reject();
			}

			// Specify the close handler for the web socket connection
			webSocket.closeHandler(aVoid -> {
				logger.info("Closing socket session : " + webSocket.textHandlerID());
				wsSessions.remove(webSocket.textHandlerID());
			});
		});

		httpServer.listen(9443, httpServerAsyncResult -> {
			logger.info("Http server up and running at port ["+ httpServer.actualPort() + "]");
		});
	}

	@Override
	public void stop() throws Exception {
		if (null != this.httpServer){
			this.httpServer.close(voidAsyncResult -> {
				if (voidAsyncResult.succeeded()){
					logger.info("Server ["+ httpServer.actualPort() + "] closed successfully");
				}
				else{
					logger.info("Server [" + httpServer.actualPort() + "] closed successfully");
				}
			});
		}
		super.stop();
	}
}
