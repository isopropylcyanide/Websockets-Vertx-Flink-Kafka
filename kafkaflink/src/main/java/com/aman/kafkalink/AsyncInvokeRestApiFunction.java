package com.aman.kafkalink;

import com.aman.kafkalink.entity.LoginRequest;
import com.aman.kafkalink.entity.LoginResponse;
import com.aman.kafkalink.entity.MessageType;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

import java.util.Collections;
import java.util.concurrent.TimeoutException;

public class AsyncInvokeRestApiFunction extends RichAsyncFunction<LoginRequest, LoginResponse> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The Asynchronous client that can issue concurrent requests with callbacks
	 */
	private transient AsyncHttpClient asyncHttpClient = null;
	
	@Override
	public void open(Configuration parameters) throws Exception {
		System.out.println("opening");
		asyncHttpClient = new DefaultAsyncHttpClient();
		
	}
	
	@Override
	public void close() throws Exception {
		System.out.println("closing");
		super.close();
		asyncHttpClient.close();
	}

	@Override
	public void timeout(LoginRequest loginRequest, ResultFuture<LoginResponse> resultFuture) throws Exception {
		resultFuture.completeExceptionally(new TimeoutException("[Api-Invocation] Timeout occurred during login"));
	}

	@Override
	public void asyncInvoke(LoginRequest loginRequest, final ResultFuture<LoginResponse> resultFuture) throws Exception {
		if (loginRequest == null || loginRequest.getUsername() == null) {
			return;
		}
		LoginResponse responseMessage = new LoginResponse();
		responseMessage.setSenderId(loginRequest.getSenderId());
		responseMessage.setSuccess(true);
		responseMessage.setResponse("Stubbed response from flink job [Async API]");
		responseMessage.setMessageType(MessageType.REST);
		resultFuture.complete(Collections.singletonList(responseMessage));

		// issue the asynchronous request, receive a future for result
		/*Gson g = new Gson();
		String jsonContent = g.toJson(loginRequest);
		Request request = asyncHttpClient.preparePost(loginRequest.getRequestUrl()).setHeader("Content-Type", "application/json")
				.setHeader("Content-Length", "" + jsonContent.length()).setBody(jsonContent).build();

		ListenableFuture<LoginResponse> loginResponseListenableFuture = null;
		try {
			loginResponseListenableFuture = asyncHttpClient.executeRequest(request, new AsyncCompletionHandler<LoginResponse>() {

				@Override
				public LoginResponse onCompleted(Response response) throws Exception {
					Gson g = new Gson();
					LoginResponse responseMessage = g.fromJson(response.getResponseBody(), LoginResponse.class);
					responseMessage.setSenderId(loginRequest.getSenderId());
					responseMessage.setSuccess(true);
					responseMessage.setResponse(response.getResponseBody());
					responseMessage.setMessageType(MessageType.REST);
					resultFuture.complete(Arrays.asList(responseMessage));
					return responseMessage;
				}

				@Override
				public void onThrowable(Throwable t) {
					resultFuture.completeExceptionally(t);
				}
			});

		} catch (Exception ex) {
			System.out.println(" exception thrown !!!!");
			ex.printStackTrace();
		} finally {
			loginResponseListenableFuture.done();
		}
		*/
	}
	
}
