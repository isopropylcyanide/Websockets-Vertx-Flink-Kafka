package com.aman.kafkalink.entity;

import com.google.gson.Gson;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class LoginResponseSerializer implements SerializationSchema<LoginResponse> {

	private static final long serialVersionUID = 6154188370181669758L;

	@Override
	public byte[] serialize(LoginResponse loginResponse) {
		Gson g = new Gson();
		String message = g.toJson(loginResponse);
		return message.getBytes();
	}
}
