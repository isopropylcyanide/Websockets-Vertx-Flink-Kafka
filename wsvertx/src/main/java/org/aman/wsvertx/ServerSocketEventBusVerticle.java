package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.shareddata.LocalMap;
import org.apache.log4j.Logger;

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
			if (webSocket.path().equals("/wsapi/register")) {
				logger.info("Request received at socket [" + webSocket.textHandlerID() + "]");
				wsSessions.put(webSocket.textHandlerID(), webSocket.textHandlerID());

				// Set handler for the incoming text data
				webSocket.textMessageHandler(data -> {
					logger.info("Trying to send to kafka topic: data [" + data + "]");
					// Send raw socket data to kafka producer event bus
					vertx.eventBus()
							.send("ws.messages.producer.event.bus", data,
									messageAsyncResult -> {
										if (messageAsyncResult.succeeded()) {
											logger.info("Message status [" + messageAsyncResult.result().body() + "]");
										}
									});

					// Receive processed data from kafka consumer and write back to the socket
					vertx.eventBus().consumer("ws-handler-" + webSocket.textHandlerID(), kafkaMessage -> {
						logger.info("Received message from Kafka at Vertx: " + kafkaMessage.body());
						webSocket.writeTextMessage(kafkaMessage.body().toString());
						kafkaMessage.reply("Writing the response to websocket");
					});
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
			//Deploy the client verticle that sends response to socket with unique id
			vertx.deployVerticle(new ClientSocketRequestVerticle());
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
