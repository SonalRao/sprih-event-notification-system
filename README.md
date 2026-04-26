                       # 📩 Event Notification System

## 🚀 Overview

This is an **asynchronous event notification system** built using **Java 21 + Spring Boot**.

The system accepts events (EMAIL, SMS, PUSH), processes them using **separate queues and worker threads**, and sends a **callback notification** upon completion.

---

## 🎯 Features

* ✅ Separate queues per event type (EMAIL, SMS, PUSH)
* ✅ FIFO processing within each queue
* ✅ Parallel processing across event types
* ✅ Asynchronous execution using worker threads
* ✅ Callback mechanism after processing
* ✅ Graceful shutdown with queue draining
* ✅ Shutdown guard (rejects new events)
* ✅ Payload validation per event type
* ✅ Failure simulation (~10%)

---

## 🧠 Architecture

```text
Client → Controller → Service → Queue → Worker → Callback → Client
```

---

## 🔁 Flow

1. Client sends request to `/api/events`
2. Request is validated and accepted (HTTP 202)
3. Event is pushed into respective queue
4. Worker thread processes event
5. Callback is sent with status (COMPLETED / FAILED)

---

## 📡 API

### POST `/api/events`

### Request Body

```json
{
  "eventType": "EMAIL",
  "payload": {
    "recipient": "user@example.com",
    "message": "Hello"
  },
  "callbackUrl": "https://webhook.site/your-id"
}
```

---

### Response

```json
{
  "eventId": "uuid",
  "message": "Event accepted for processing"
}
```

---

## 🔔 Callback Mechanism

After processing, system sends:

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
  "processedAt": "..."
}
```

---

## 🧪 Testing Callback

Use:

👉 https://webhook.site

Steps:

1. Open site
2. Copy unique URL
3. Use as `callbackUrl`
4. Observe POST requests

---

## ⚙️ Processing Delays

| Event Type | Delay |
| ---------- | ----- |
| EMAIL      | 5 sec |
| SMS        | 3 sec |
| PUSH       | 2 sec |

---

## 🧵 Concurrency Model

* BlockingQueue for thread-safe communication
* One worker thread per event type
* FIFO guaranteed within each queue
* Parallel execution across queues

---

## 🛑 Graceful Shutdown

* Stops accepting new events
* Drains remaining queue
* Completes in-progress events
* Shuts down threads cleanly

---

## ⚠️ Validation

* eventType must be valid enum
* payload fields:

    * EMAIL → recipient, message
    * SMS → phoneNumber, message
    * PUSH → deviceId, message
* callbackUrl must not be blank

---

## 🧠 Design Decisions

### Why BlockingQueue?

* Thread-safe
* FIFO guarantee
* No busy-waiting

---

### Why separate queues?

* Isolation
* Parallelism
* Scalability

---

### Why async?

* Fast API response
* Better user experience

---

### Why UUID?

* Global uniqueness
* No collision risk

---

## ⚠️ Assumptions

* In-memory queues (no persistence)
* At-least-once callback delivery
* Single instance system

---

## 🚀 Future Improvements

* Retry mechanism for failed callbacks
* Persistent queues (Kafka/RabbitMQ)
* Distributed workers
* Monitoring and metrics

---

## ⚙️ How to Run

### Using Maven

```bash
mvn clean install
mvn spring-boot:run
```

---

### Using Docker

```bash
docker compose up
```

App runs on:

```
http://localhost:8080
```

---

## 🧪 Running Tests

```bash
mvn test
```