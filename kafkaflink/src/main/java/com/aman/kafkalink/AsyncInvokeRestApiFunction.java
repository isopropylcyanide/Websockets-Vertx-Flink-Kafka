package com.aman.kafkalink;

import com.aman.kafkalink.entity.LoginRequest;
import com.aman.kafkalink.entity.LoginResponse;
import com.aman.kafkalink.entity.MessageType;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.log4j.Logger;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

import java.util.Collections;
import java.util.concurrent.TimeoutException;

public class AsyncInvokeRestApiFunction extends RichAsyncFunction<LoginRequest, LoginResponse> {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AsyncInvokeRestApiFunction.class);

	/**
	 * The Asynchronous client that can issue concurrent requests with callbacks
	 */
	private transient AsyncHttpClient asyncHttpClient = null;
	
	@Override
	public void open(Configuration parameters) throws Exception {
		logger.info("Opening connection " + parameters.toString());
		asyncHttpClient = new DefaultAsyncHttpClient();
		
	}
	
	@Override
	public void close() throws Exception {
		logger.info("Closing connection");
		super.close();
		asyncHttpClient.close();
	}

	@Override
	public void timeout(LoginRequest loginRequest, ResultFuture<LoginResponse> resultFuture) throws Exception {
		resultFuture.completeExceptionally(new TimeoutException("[Api-Invocation] Timeout occurred during login"));
	}

	@Override
	public void asyncInvoke(LoginRequest loginRequest, final ResultFuture<LoginResponse> resultFuture) throws Exception {
		LoginResponse responseMessage = new LoginResponse();
		responseMessage.setSenderId(loginRequest.getSenderId());
		responseMessage.setSuccess(true);
		responseMessage.setResponse("Stubbed response from flink job [Async API]");
		responseMessage.setMessageType(MessageType.REST);
		resultFuture.complete(Collections.singletonList(responseMessage));
	}
	
}
