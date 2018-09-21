# Websockets-Vertx-Kafka-Flink
### A simple request response cycle using Websockets, Eclipse Vert-x server, Apache Kafka, Apache Flink. ###
---

* #### An incoming request gets routed to a non blocking Vertx server which then writes the request to a specific Kafka topic. ####
* #### A Flink consumer implemented as another side project consumes the messages from the given request topic ####
* #### [Optional] Flink job hits a Rest API hosted on a Spring boot server. You can use Jax-Rs or even hardcode the response ####
* #### Flink writes the API result to another topic. Every message has a unique sender id. Flink sends the response with the same ####
* #### Finally the Vertx Kafka consumer listens for responses from the response topic and sends an event to a websocket handler  ####
* #### Websocket consumer for a specific id writes the response to its own socket thus completing the entire async request cycle ####


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
 
 ### What you do in the Flink Job depends on the use case. Options are ###
* Make async rest API call
* Interact with a database using an async clients
* Return a mock response

### Caveats ###
* Here, we are making a request using the AsyncHTTP Client to an endpoint hosted on a Spring Boot Server
* The rest API Server is listening on port 9004
* You are free to experiment in this department.
* If you choose to continue using the Rest API given in this project, make sure you have an endpoint implementation.
 
### Setting up the project ###
 * Run the kafka-flink connector project that waits for incoming data stream from kafka queue "flink_resp"
 * Run the ws-vertx project that invokes an event on the event bus which writes a sample API request to the topic.
 * Verify that the message is written correctly on the topic "flink-demo"
 * Flink Kafka connector consumes the message, serializes it, transforms the data stream into a response stream
 * Flink job now writes the response back to the response topic "flink-demo-resp"

### Testing the web socket flow ###
* Incuded within the vertx flow is a client socket verticle that emulates a single web socket request
* It is fired as soon as the server verticle is deployed. [Optional] Look for the following
```
  # Uncomment the below line for local UI testing: It creates a websocket request to the given server
 //vertx.deployVerticle(new ClientSocketRequestVerticle());

```

* You can however choose to send websocket requests from a client manually. Use the following
```
   # Use the following websocket URL
   ws://127.0.0.1:9443/wsapi/register

   # Once the socket opens, begin sending messages in the correct format
   {
	    "email": "your email",
	    "password": "your password ",
	    "registerAsAdmin": true
   }

```

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
