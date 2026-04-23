# Smart Campus API

A RESTful API for managing university campus rooms and sensors, built with JAX-RS (Jersey) and Maven.

## API Overview

This API provides endpoints to manage:
- **Rooms** — create, retrieve, and delete campus rooms
- **Sensors** — register sensors linked to rooms, filter by type
- **Sensor Readings** — log and retrieve historical readings per sensor

All data is stored in-memory using HashMaps. No database is used.

Base URL: `http://localhost:8080/SmartCampusAPI/api/v1`

---

## How to Build and Run

### Prerequisites
- Java JDK 11 or higher
- Apache Maven
- Apache Tomcat 9
- Apache NetBeans 28 (or any IDE)

### Steps

1. Clone the repository:
   git clone https://github.com/NadithiMoonasingha/SmartCampusAPI.git

2. Open the project in NetBeans:
   - File → Open Project → select the SmartCampusAPI folder

3. Add Apache Tomcat 9 as server:
   - Tools → Servers → Add Server → Apache Tomcat
   - Browse to your Tomcat installation folder

4. Build the project:
   - Right-click project → Clean and Build

5. Run the project:
   - Right-click project → Run
   - Server starts at `http://localhost:8080`

6. Test the API:
   - Open Postman or browser
   - Navigate to `http://localhost:8080/SmartCampusAPI/api/v1`

---

## Sample curl Commands

### 1. Get API Discovery Info
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1
```

### 2. Create a Room
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":50}'
```

### 3. Get All Rooms
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 4. Create a Sensor
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":0.0,"roomId":"LIB-301"}'
```

### 5. Get Sensors Filtered by Type
```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=Temperature"
```

### 6. Add a Sensor Reading
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":23.5}'
```

### 7. Get All Readings for a Sensor
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings
```

### 8. Try Deleting a Room With Sensors (409 Error)
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

---

## Report — Answers to Coursework Questions

### Part 1 — JAX-RS Resource Lifecycle
By default, JAX-RS creates a **new instance** of each resource class for every incoming HTTP request (per-request lifecycle). This means resource classes are not singletons. Because of this, any instance variables would be reset on each request and cannot be used to store shared state. To share data across requests, static data structures must be used — in this project, the `DataStore` class holds static HashMaps for rooms, sensors, and readings. These are shared across all requests safely because they exist at the class level, not the instance level.

### Part 1 — HATEOAS
HATEOAS (Hypermedia as the Engine of Application State) means that API responses include links to related resources, allowing clients to navigate the API dynamically without needing to hardcode URLs. This benefits client developers because they do not need to memorise or look up endpoint paths — the API itself tells them where to go next. For example, the discovery endpoint returns links to `/api/v1/rooms` and `/api/v1/sensors`, guiding the client automatically.

### Part 2 — Full Objects vs IDs in Lists
Returning full room objects in a list provides all information in a single request, reducing the number of API calls a client must make. However, it increases network bandwidth and response size, especially with many rooms. Returning only IDs is more lightweight but forces the client to make additional requests to fetch each room's details, increasing latency. For most use cases, returning full objects is more practical unless the dataset is very large.

### Part 2 — Idempotency of DELETE
Yes, DELETE is idempotent in this implementation. The first DELETE request removes the room and returns 200 OK. Any subsequent DELETE request for the same room returns 404 Not Found because the room no longer exists. The server state does not change after the first deletion — the room is gone regardless of how many times the request is sent. This satisfies the definition of idempotency: multiple identical requests produce the same server state.

### Part 3 — @Consumes and Content-Type Mismatch
The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells JAX-RS that the endpoint only accepts `application/json` content. If a client sends data with a different Content-Type such as `text/plain` or `application/xml`, JAX-RS will automatically return a **415 Unsupported Media Type** response without even invoking the resource method. This protects the API from receiving data it cannot parse.

### Part 3 — @QueryParam vs Path-Based Filtering
Using `@QueryParam` for filtering (e.g. `/sensors?type=CO2`) is preferred because query parameters are semantically designed for optional filtering and searching of collections. A path-based approach (e.g. `/sensors/type/CO2`) implies that `type` and `CO2` are fixed resources in the hierarchy, which is misleading. Query parameters can also be combined easily (e.g. `?type=CO2&status=ACTIVE`), while path-based filtering would require separate endpoints for each combination.

### Part 4 — Sub-Resource Locator Pattern
The Sub-Resource Locator pattern delegates handling of nested paths to separate resource classes. Instead of defining all endpoints in one large class, logic is split into focused classes such as `SensorReadingResource`. This improves maintainability, readability, and testability. Each class has a single responsibility. In large APIs with many nested resources, this prevents any single class from becoming unmanageably large and makes it easier to extend or modify individual sections independently.

### Part 5 — HTTP 422 vs 404
HTTP 404 means "the requested resource was not found" — it implies the URL itself points to something that does not exist. HTTP 422 Unprocessable Entity is more semantically accurate when the request URL is valid but the payload contains a reference to a missing resource (such as a non-existent `roomId`). The issue is not with the endpoint but with the data inside the request body. Using 422 clearly communicates that the server understood the request but could not process it due to invalid data references.

### Part 5 — Security Risks of Stack Traces
Exposing Java stack traces to external users is a serious security risk. Stack traces reveal internal class names, package structures, method names, line numbers, and library versions. An attacker can use this information to identify which frameworks and versions are in use, then look up known vulnerabilities for those versions. They can also understand the internal architecture of the application to craft more targeted attacks. The global exception mapper prevents this by catching all unhandled exceptions and returning only a generic error message.

### Part 5 — Filters vs Inline Logging
Using JAX-RS filters for cross-cutting concerns like logging is superior to inserting `Logger.info()` calls in every resource method because filters apply automatically to every request and response without modifying any resource code. This follows the principle of separation of concerns — resource methods focus on business logic, while filters handle infrastructure concerns. If logging requirements change, only the filter needs updating rather than every individual method. It also ensures no endpoint is accidentally missed.
