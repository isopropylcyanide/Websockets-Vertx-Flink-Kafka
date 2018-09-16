package com.aman.kafkalink.entity;

import com.google.gson.Gson;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;

import java.io.IOException;

public class LoginRequestSerializer implements DeserializationSchema<LoginRequest>, SerializationSchema<LoginRequest> {

	private static final long serialVersionUID = 6154188370181669758L;

	public TypeInformation<LoginRequest> getProducedType() {
		return TypeExtractor.getForClass(LoginRequest.class);
	}


	public byte[] serialize(LoginRequest element) {
		Gson g = new Gson();
		String message = g.toJson(element);
		return message.getBytes();
	}


	public LoginRequest deserialize(byte[] message) throws IOException {
		String strMessage = new String(message);
		return new Gson().fromJson(strMessage, LoginRequest.class);
	}


	public boolean isEndOfStream(LoginRequest nextElement) {
		return false;
	}

	
}
