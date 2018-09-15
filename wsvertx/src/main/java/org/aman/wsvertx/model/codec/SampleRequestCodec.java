package org.aman.wsvertx.model.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;
import org.aman.wsvertx.model.payload.ApiRequest;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;

public class SampleRequestCodec implements MessageCodec<ApiRequest, ApiRequest> {

	private static final Logger logger = Logger.getLogger(SampleRequestCodec.class);

	@Override
	public void encodeToWire(Buffer buffer, ApiRequest apiRequest) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String jsonToStr = ow.writeValueAsString(apiRequest);
			int length = jsonToStr.getBytes().length;
			buffer.appendInt(length);
			buffer.appendString(jsonToStr);
		} catch (JsonProcessingException e) {
			logger.error("Error encoding [" + apiRequest + "] from " + this.name());
		}
	}

	@Override
	public ApiRequest decodeFromWire(int position, Buffer buffer) {
		int length = buffer.getInt(position);
		// Get JSON string by it`s length
		// Jump 4 because getInt() == 4 bytes
		String jsonStr = buffer.getString(position += 4, position += length);
		JsonObject contentJson = new JsonObject(jsonStr);
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(new StringReader(jsonStr), ApiRequest.class);
		} catch (IOException e) {
			logger.error("Error decoding [" + jsonStr + "] to " + this.name() );
		}
		return null;
	}

	@Override
	public ApiRequest transform(ApiRequest apiRequest) {
		return apiRequest;
	}

	@Override
	public String name() {
		return this.getClass().getName();
	}

	@Override
	public byte systemCodecID() {
		return -1;
	}
}
