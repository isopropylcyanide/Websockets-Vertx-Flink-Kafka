package com.aman.kafkalink.entity;

import com.google.gson.Gson;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class RegisterResponseSerializer implements SerializationSchema<RegisterResponse> {

	private static final long serialVersionUID = 6154188370181669751L;

	@Override
	public byte[] serialize(RegisterResponse registerResponse) {
		Gson g = new Gson();
		String message = g.toJson(registerResponse);
		return message.getBytes();
	}
}
