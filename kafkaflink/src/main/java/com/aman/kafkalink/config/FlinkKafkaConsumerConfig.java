package com.aman.kafkalink.config;

import java.util.Properties;

public class FlinkKafkaConsumerConfig {

	/**
	 * Generate the properties for the kafka consumer
	 */
	public static Properties getKafkaConsumerConfig() {
		Properties prop = new Properties();
		prop.setProperty("topic", "flink-demo");
		prop.setProperty("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
		prop.setProperty("zookeeper.connect", "localhost:2181");
		prop.setProperty("group.id", "flink-login-request-consumer-group");
		prop.setProperty("enable.auto.commit", "true");
		return prop;

	}
}
