# 📩 Event Notification System

## 🚀 Overview

This project is an **asynchronous Event Notification System** built using **Java + Spring Boot**.

The system accepts different types of notification events via REST APIs, processes them asynchronously using **separate queues and worker threads**, and notifies clients of the final status via a **callback mechanism**.

---

## 🎯 Features

* Accepts 3 types of events:

    * EMAIL
    * SMS
    * PUSH

* Asynchronous processing using:

    * `BlockingQueue`
    * Dedicated worker threads per event type

* FIFO (First-In-First-Out) processing per queue

* Simulated processing delays:

    * EMAIL → 5 seconds
    * SMS → 3 seconds
    * PUSH → 2 seconds

* Random failure simulation (~10%)

* Callback mechanism to notify client on completion/failure

* Graceful shutdown:

    * Stops accepting new events
    * Completes in-progress events
    * Shuts down threads cleanly

---

## 🧠 Architecture

```
Client → REST API → Queue Manager → Worker Threads → Callback Service
```

### Flow:

1. Client sends event request
2. API validates and accepts request
3. Event is pushed to corresponding queue
4. Worker thread processes event asynchronously
5. System sends callback to client with final status

---

## 📦 Tech Stack

* Java 21
* Spring Boot
* Maven
* JUnit (for testing)
* Docker

---

## 📡 API Endpoint

### POST `/api/events`

#### Request Body

```json
{
  "eventType": "EMAIL",
  "payload": {
    "recipient": "user@example.com",
    "message": "Welcome!"
  },
  "callbackUrl": "https://https://webhook.site/fae6a8bd-6ab7-4f37-bfa2-a922726d3e73"
}
```

#### Response

```json
{
  "eventId": "uuid",
  "message": "Event accepted for processing"
}
```

---

## 🔁 Callback Payload

### Success

```json
{
  "eventId": "uuid",
  "status": "COMPLETED",
  "eventType": "EMAIL",
  "processedAt": "2026-04-25T12:34:56Z"
}
```

### Failure

```json
{
  "eventId": "uuid",
  "status": "FAILED",
  "eventType": "EMAIL",
  "errorMessage": "Simulated processing failure",
  "processedAt": "2026-04-25T12:34:56Z"
}
```

---

## 🧪 Testing Callback

You can test callbacks using:

👉 https://webhook.site

1. Open the site
2. Copy your unique URL
3. Use it as `callbackUrl`
4. Observe incoming POST requests

Example:
"callbackUrl": "https://webhook.site/your-unique-id"

---

## ⚙️ How to Run

### 🔹 Using Maven

```bash
mvn clean install
mvn spring-boot:run
```

---

### 🔹 Using Docker

```bash
docker compose up
```

Application will run at:

```
http://localhost:8080
```

---

## 🧵 Concurrency Design

* Each event type has its own `BlockingQueue`
* Each queue is processed by a dedicated worker thread
* Ensures:

    * Thread safety
    * FIFO processing
    * Parallel execution across event types

---

## 🛑 Graceful Shutdown

Implemented using `@PreDestroy` and `ExecutorService`.

On shutdown:

* Stops accepting new requests
* Allows queued events to finish
* Terminates threads cleanly

---

## 🧪 Unit Testing

Includes tests for:

* Event routing to correct queue
* API validation
* Failure simulation
* Callback invocation (mocked)

---

## 💡 Design Decisions

### Why BlockingQueue?

* Thread-safe
* Built-in FIFO guarantee
* Avoids busy waiting

---

### Why separate queues per event type?

* Isolation of workloads
* Parallel processing
* Better scalability

---

### Why async processing?

* API responds instantly
* Improves performance and user experience

---

### Why UUID for eventId?

* Ensures global uniqueness
* Avoids collisions across systems

---

## ⚠️ Assumptions

* In-memory queues (no persistence)
* At-least-once callback delivery
* Single instance application

---

## 🚀 Future Improvements

* Retry mechanism for failed callbacks
* Persistent queue (Kafka / RabbitMQ)
* Distributed processing
* Monitoring & metrics


