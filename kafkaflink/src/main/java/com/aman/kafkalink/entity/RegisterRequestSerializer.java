package com.aman.kafkalink.entity;

import com.google.gson.Gson;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;

import java.io.IOException;

public class RegisterRequestSerializer implements DeserializationSchema<RegisterRequest>,
		SerializationSchema<RegisterRequest> {

	private static final long serialVersionUID = 6154188370181669711L;

	public TypeInformation<RegisterRequest> getProducedType() {
		return TypeExtractor.getForClass(RegisterRequest.class);
	}


	public byte[] serialize(RegisterRequest element) {
		Gson g = new Gson();
		String message = g.toJson(element);
		return message.getBytes();
	}


	public RegisterRequest deserialize(byte[] message) throws IOException {
		String strMessage = new String(message);
		return new Gson().fromJson(strMessage, RegisterRequest.class);
	}


	public boolean isEndOfStream(RegisterRequest nextElement) {
		return false;
	}


}
