# ws-vertx-flink-kafka
### A simple request response cycle using Websockets, Eclipse Vert-x server, Apache Kafka, Apache Flink. ###
---

![image](https://user-images.githubusercontent.com/12872673/45586253-6a12ce00-b911-11e8-9508-3536f101717c.png)

* #### An incoming request gets routed to a non blocking Vertx server which then writes the request to a specific Kafka topic. ####
* #### A Flink consumer implemented as another side project consumes the messages from the above topic, processes it and writes the 
* #### result to another queue. Every message has a unique sender id. Finally the Vertx Kafka consumer listens for responses from ####
* #### the response queue and sends the result back to the websocket handler thus completing the entire async web request cycle ####


![image](https://user-images.githubusercontent.com/12872673/45586212-78acb580-b910-11e8-9d7a-9a3a85f22419.png)                             ![image](https://user-images.githubusercontent.com/12872673/45586233-ebb62c00-b910-11e8-9fc7-d48a73bcd31d.png)

---
### Prerequisites ###
* Java 1.8
* Apache Kafka 2.0.0
* Apache Zookeeper 3.4.8
* Eclipse Vertx 3.5.3
* Apache Flink 1.6.0
---


### Setting up Apache Kafka ###
```
  # Start Zookeeper instance 
  $ zookeeper-server-start.bat ..\..\config\zookeeper.properties
  
  # Start Kafka server
  $ kafka-server-start.bat ..\..\config\server.properties
  
  # Create a request topic
  $ kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic flink-demo

  # Create a response queue
  $ kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic flink-demo-resp
  
  # Verify the consumer of request queue flink-demo
  $ kafka-console-consumer.bat --bootstrap-server localhost:9092 --from-beginning --topic flink-demo

  # Verify the consumer of response queue flink-demo-resp
  $ kafka-console-consumer.bat --bootstrap-server localhost:9092 --from-beginning --topic flink-demo-resp
  
```
 Make sure following is appended to **config\server.properties**
 ```
 port = 9092
 advertised.host.name = localhost 
 ```
 
 Note: Replace .bat files with .sh files when working in a UNIX environment.
 
 ---
 
 
 ### Setting up the project ###
 * Run the kafka-flink connector project that waits for incoming data stream from kafka queue "flink_resp"
 * Run the ws-vertx project that invokes an event on the event bus which writes a sample API request to the topic.
 * Verify that the message is written correctly on the topic "flink-demo"
 * Flink Kafka connector consumes the message, serializes it, transforms the data stream into a response stream
 * Flink job now writes the response back to the response topic "flink-demo-resp"


----
### Websockets ###
* Websocket for communication between app & backend
* Async messages, non-blocking communication layer
* Full duplex communication channels over single TCP

---
### Vert-x ###
* A toolkit ecosystem, to build reactive application on JVM
* Vert-x library helps implement non-blocking asynchronous event bus implementation.
* Helps manage Websocket queue

---
### Kafka ###
* Distributed streaming platform.
* Kafka provides a fully integrated Streams API to allow an application to act as a stream processor, consuming an input stream from one or more topics and producing an output stream to one or more output topics, effectively transforming the input streams to output streams.
* Handles out-of-order data.

---
### Flink ###
* Open-source platform for distributed stream and batch data processing.
* Provides data distribution, communication, and fault tolerance for distributed computations over data streams. 
* Builds batch processing on top of the streaming engine, overlaying native iteration support, managed memory, and program optimization.
