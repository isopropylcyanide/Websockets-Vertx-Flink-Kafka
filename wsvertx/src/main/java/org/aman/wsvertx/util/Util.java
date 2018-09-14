package org.aman.wsvertx.util;

import java.util.UUID;

public class Util {

	/**
	 * Generates a random string using the default UUID
	 */
	public static String generateRandomUUID(){
		return UUID.randomUUID().toString();
	}
}
