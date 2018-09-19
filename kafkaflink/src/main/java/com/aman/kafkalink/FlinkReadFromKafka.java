package com.aman.kafkalink;

import com.aman.kafkalink.entity.RegisterRequest;
import com.aman.kafkalink.entity.RegisterRequestSerializer;
import com.aman.kafkalink.entity.RegisterResponse;
import com.aman.kafkalink.entity.RegisterResponseSerializer;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.async.AsyncFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.util.Collector;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class FlinkReadFromKafka {

	private static final Logger logger = Logger.getLogger(FlinkReadFromKafka.class);
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		Properties prop = getKafkaConsumerConfig();
		// Create a flink consumer from the topic with a custom serializer for "RegisterRequest"
		FlinkKafkaConsumer010<RegisterRequest> consumer = new FlinkKafkaConsumer010<>(prop.getProperty("topic"),
				new RegisterRequestSerializer(), prop);

		consumer.setStartFromLatest();

		// Create a flink data stream from the consumer source i.e Kafka topic
		DataStream<RegisterRequest> messageStream = env.addSource(consumer);
		logger.info(messageStream.process(new ProcessFunction<RegisterRequest, Object>() {
			@Override
			public void processElement(RegisterRequest RegisterRequest, Context context, Collector<Object> collector) throws Exception {
				logger.info("Processing incoming request " + RegisterRequest);
			}
		}));

		//Function that defines how a datastream object would be transformed from within flink
		AsyncFunction<RegisterRequest, RegisterResponse> loginRestTransform = new AsyncInvokeRestApiFunction();

		//Transform the datastream in parallel
		DataStream<RegisterResponse> result = AsyncDataStream
						.unorderedWait(messageStream, loginRestTransform, 1000L, TimeUnit.MILLISECONDS, 20)
						.setParallelism(20);

		//Write the result back to the Kafka sink i.e response topic
		result.addSink(new FlinkKafkaProducer010<>("flink-demo-resp", new RegisterResponseSerializer(),
				getKafkaConsumerConfig()));
		env.execute();
	}


	/**
	 * Generate the properties for the kafka consumer
	 */
	public static Properties getKafkaConsumerConfig() {
		Properties prop = new Properties();
		prop.setProperty("topic", "flink-demo");
		prop.setProperty("bootstrap.servers", "localhost:9092");
		prop.setProperty("zookeeper.connect", "localhost:2181");
		prop.setProperty("group.id", "flink-login-request-consumer-group");
		prop.setProperty("enable.auto.commit", "true");
		return prop;
		
	}
}
