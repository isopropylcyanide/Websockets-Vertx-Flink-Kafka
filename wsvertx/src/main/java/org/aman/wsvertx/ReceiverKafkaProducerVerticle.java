package org.aman.wsvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;
import org.aman.wsvertx.config.KafkaProducerConfig;
import org.aman.wsvertx.model.payload.ApiRequest;
import org.apache.log4j.Logger;

public class ReceiverKafkaProducerVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.getLogger(ReceiverKafkaProducerVerticle.class);

	private String topic;
	private KafkaProducer<String, String> kafkaProducer;

	public ReceiverKafkaProducerVerticle(String topic) {
		this.topic = topic;
		this.kafkaProducer = null;
	}

	public void start(Future<Void> startFuture) {
		logger.info("Deployed verticle that sends to Kafka Topic[" + topic + "]");

		vertx.eventBus().consumer("kafka.queue.publisher", message -> {
			ApiRequest apiRequest = (ApiRequest) message.body();
			logger.info(this.topic + " received message: " + apiRequest);
			this.kafkaProducer = KafkaProducerConfig.getKafkaProducerConfig(vertx);
			KafkaProducerRecord<String, String> kafkaProducerRecord =
					KafkaProducerRecord.create(this.topic, apiRequest.toString());
			kafkaProducer.write(kafkaProducerRecord, done -> {
				if (done.succeeded()) {
					RecordMetadata recordMetadata = done.result();
					System.out.println("Message " + kafkaProducerRecord.value() + " written on topic=" + recordMetadata.getTopic() +
							", partition=" + recordMetadata.getPartition() +
							", offset=" + recordMetadata.getOffset());
					message.reply("Published to Kafka");
				}
			});

		}).completionHandler(voidAsyncResult -> {
			if (voidAsyncResult.succeeded()){
				logger.info("kafka.queue.publisher handler set up successful");
			}
			else{
				logger.info("kafka.queue.publisher handler set up failed");
			}
		});
	}
}