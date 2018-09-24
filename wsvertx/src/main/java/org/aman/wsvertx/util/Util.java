package org.aman.wsvertx.util;

import org.aman.wsvertx.model.payload.RegisterRequest;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;

public class Util {

	private static final Logger logger = Logger.getLogger(Util.class);

	/**
	 * Generates a random string using the default UUID
	 */
	public static String generateRandomUUID(){
		return UUID.randomUUID().toString();
	}

	/**
	 * Tries to deserialize the json payload into a register request with the schema
	 */
	public static Optional<RegisterRequest> getRegisterRequestFromJsonString(String payloadString) {
		try {
			DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(payloadString.getBytes()));
			Schema registerRequestSchema = RegisterRequest.getClassSchema();

			Decoder decoder = DecoderFactory.get().jsonDecoder(registerRequestSchema, dataInputStream);
			DatumReader<RegisterRequest> reader = new GenericDatumReader<>(registerRequestSchema);
			RegisterRequest registerRequest = reader.read(null, decoder);
			return Optional.ofNullable(registerRequest);
		} catch (IOException e) {
			logger.info("Failed to get AVRO register payload string [" + payloadString + "]");
			return Optional.empty();
		}
	}


	/**
	 * Returns an encoded JSON string for the given Avro object.
	 * @param record is the record to encode
	 * @return the JSON string representing this Avro object.
	 * @exception IOException if there is an error.
	 */
	public static Optional<String> getJsonString(GenericContainer record) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			JsonEncoder encoder = EncoderFactory.get().jsonEncoder(record.getSchema(), os);
			DatumWriter<GenericContainer> writer = new GenericDatumWriter<GenericContainer>();
			if (record instanceof SpecificRecord) {
				writer = new SpecificDatumWriter<GenericContainer>();
			}

			writer.setSchema(record.getSchema());
			writer.write(record, encoder);
			encoder.flush();
			String jsonString = new String(os.toByteArray(), Charset.forName("UTF-8"));
			os.close();
			return Optional.ofNullable(jsonString);
		} catch (IOException e) {
			logger.error(e);
			return Optional.empty();
		}
	}
}
