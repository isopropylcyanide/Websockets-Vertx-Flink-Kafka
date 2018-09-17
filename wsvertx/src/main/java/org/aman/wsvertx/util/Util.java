package org.aman.wsvertx.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

public class Util {

	/**
	 * Generates a random string using the default UUID
	 */
	public static String generateRandomUUID(){
		return UUID.randomUUID().toString();
	}

	public static Optional<String> getJsonStringFromObject(Object object){
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return Optional.ofNullable(objectMapper.writeValueAsString(object));
		} catch (JsonProcessingException e) {
			return Optional.empty();
		}
	}
}
