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

import java.util.Collections;


public class AsyncRegisterApiInvocation extends RichAsyncFunction<RegisterRequest, RegisterResponse> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AsyncRegisterApiInvocation.class);
	private final Integer apiTimeoutMs;

	/**
	 * The Asynchronous client that can issue concurrent requests with callbacks
	 */
	private transient AsyncHttpClient asyncHttpClient = null;

	public AsyncRegisterApiInvocation(Integer apiTimeoutMs) {
		this.apiTimeoutMs = apiTimeoutMs;
	}

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
	public void timeout(RegisterRequest registerRequest, ResultFuture<RegisterResponse> resultFuture) throws Exception {
		RegisterResponse registerResponse = new RegisterResponse();
		registerResponse.setSuccess(false);
		registerResponse.setSenderId(registerRequest.getSenderId());
		registerResponse.setError("[TimeoutException Api-Invocation]");
		registerResponse.setCause("Timeout occurred during registration");
		resultFuture.complete(Collections.singletonList(registerResponse));
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
				.setRequestTimeout(this.apiTimeoutMs)
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
					responseMessage.setData(response.getResponseBody());
					responseMessage.setMessageType(MessageType.REST);
					responseMessage.setMessageType(MessageType.REST);
					resultFuture.complete(Collections.singletonList(responseMessage));
					return responseMessage;
				}

				@Override
				public void onThrowable(Throwable t) {
					RegisterResponse registerResponse = new RegisterResponse();
					registerResponse.setSuccess(false);
					registerResponse.setSenderId(registerRequest.getSenderId());
					registerResponse.setError(t.getMessage());
					registerResponse.setCause(t.getCause().getMessage());
					resultFuture.complete(Collections.singletonList(registerResponse));
				}
			});

		} catch (Exception ex) {
			logger.error("Exception [HTTP] Client " + ex);
		}
	}


}
