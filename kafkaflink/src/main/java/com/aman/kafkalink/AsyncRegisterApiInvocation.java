package com.aman.kafkalink;

import com.aman.kafkalink.entity.MessageType;
import com.aman.kafkalink.entity.RegisterRequest;
import com.aman.kafkalink.entity.RegisterResponse;
import com.google.gson.Gson;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.log4j.Logger;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.rmi.ServerException;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

public class AsyncRegisterApiInvocation extends RichAsyncFunction<RegisterRequest, RegisterResponse> {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AsyncRegisterApiInvocation.class);

	/**
	 * The Asynchronous client that can issue concurrent requests with callbacks
	 */
	private transient AsyncHttpClient asyncHttpClient = null;
	
	@Override
	public void open(Configuration parameters) {
		logger.info("Opening connection " + parameters.toString());
		this.asyncHttpClient = new DefaultAsyncHttpClient();
		
	}
	
	@Override
	public void close() throws Exception {
		logger.info("Closing connection");
		super.close();
		asyncHttpClient.close();
	}

	@Override
	public void timeout(RegisterRequest RegisterRequest, ResultFuture<RegisterResponse> resultFuture) throws Exception {
		resultFuture.completeExceptionally(new TimeoutException("[Api-Invocation] Timeout occurred during login"));
	}

	@Override
	public void asyncInvoke(RegisterRequest registerRequest, final ResultFuture<RegisterResponse> resultFuture) throws Exception {
		// issue the asynchronous request, receive a future for result
		Gson g = new Gson();
		String jsonContent = g.toJson(registerRequest);
		Request request = asyncHttpClient.preparePost("http://localhost:9004/api/auth/register").setHeader("Content" +
				"-Type", "application" +
				"/json")
				.setHeader("Content-Length", "" + jsonContent.length()).setBody(jsonContent)
				.setBody(jsonContent)
				.build();

		try {
			asyncHttpClient.executeRequest(request, new AsyncCompletionHandler<RegisterResponse>() {
				@Override
				public RegisterResponse onCompleted(Response response) throws Exception {
					logger.info("Spring returned" + response.getResponseBody());
					Gson g = new Gson();
					RegisterResponse responseMessage = g.fromJson(response.getResponseBody(),
							RegisterResponse.class);

					responseMessage.setSenderId(registerRequest.getSenderId());
					responseMessage.setSuccess(true);
					responseMessage.setData("Registration successful. Stubbed response from flink job [Async API]" + response.getResponseBody());
					responseMessage.setMessageType(MessageType.REST);
					responseMessage.setMessageType(MessageType.REST);
					resultFuture.complete(Collections.singletonList(responseMessage));
					return responseMessage;
				}

				@Override
				public void onThrowable(Throwable t) {
					resultFuture.completeExceptionally(new ServerException(t.getMessage()));
				}
			});

		} catch (Exception ex) {
			logger.error("Exception [HTTP] Client " + ex);
		}
	}


}
