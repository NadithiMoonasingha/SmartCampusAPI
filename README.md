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

---

### **Part 1 — Service Architecture & Setup**

#### **1.1 Project & Application Configuration**
In JAX-RS, resource classes are typically instantiated per request. This means a new instance of the resource class is created for each incoming HTTP request, preventing unintended sharing of request-specific data between clients.

However, this application uses shared in-memory data structures such as HashMap and ArrayList to store rooms, sensors, and readings. These structures are shared across all requests and therefore represent the actual state of the application.

Because multiple requests may access or modify these shared collections concurrently, issues such as race conditions, inconsistent updates, or data loss can occur. To address this, thread-safe approaches should be used, such as ConcurrentHashMap or synchronizing critical write operations.

Therefore, while JAX-RS resource instances are stateless and request-scoped, explicit handling is required to ensure the safety of shared in-memory data.

---

#### **1.2 The ”Discovery” Endpoint**
HATEOAS (Hypermedia as the Engine of Application State) is a key principle of RESTful design in which API responses include links to related resources.

This approach allows clients to dynamically navigate the API without relying on external documentation. For example, the discovery endpoint provides links to primary resources such as rooms and sensors.

This improves usability, reduces coupling between client and server, and allows the system to evolve without breaking client applications.

---

### **Part 2 — Room Management**

#### **2.1 Room Resource Implementation**
There is a trade-off between returning full objects and only IDs when listing resources.

Returning full objects provides complete information in a single response, reducing the need for additional API calls and simplifying client-side logic. However, it increases payload size and bandwidth usage.

Returning only IDs reduces response size and improves performance for large datasets but requires clients to make additional requests to retrieve full details.

In this implementation, returning full objects is more suitable due to the relatively small dataset and improved usability.

---

#### **2.2 Room Deletion & Safety Logic**
The DELETE operation is idempotent.

When a DELETE request is made for a room, the first request successfully removes the room. If the same request is repeated, the server returns a 404 Not Found response because the room no longer exists.

Although the response changes, the server state remains unchanged after the first request. This satisfies the definition of idempotency.

If deletion is blocked due to assigned sensors, repeated requests also do not alter the server state, further confirming idempotency.

---

### **Part 3 — Sensor Operations & Linking**

#### **3.1 Sensor Resource & Integrity**
The annotation @Consumes(MediaType.APPLICATION_JSON) ensures that the resource method only accepts requests with a JSON payload.

If a client sends data using a different media type, such as text/plain or application/xml, JAX-RS will automatically reject the request and return a 415 Unsupported Media Type response.

This enforces a clear contract between client and server and ensures that only valid and expected data formats are processed.

---

#### **3.2 Filtered Retrieval & Search**
Using query parameters for filtering, such as /sensors?type=CO2, is preferred over path-based filtering.

Query parameters are designed for optional filtering and searching of collections. They also allow multiple filters to be combined, such as /sensors?type=CO2&status=ACTIVE.

Path-based filtering implies a fixed resource hierarchy and is less flexible, making query parameters the more appropriate and scalable approach.

---

### **Part 4 — Deep Nesting with Sub - Resources**

#### **4.1 Sub-Resource Locator Pattern**
The Sub-Resource Locator pattern delegates handling of nested resources to separate classes.

For example, the path /sensors/{sensorId}/readings is handled by a dedicated resource class rather than being managed within a single large controller.

This improves separation of concerns, enhances readability, and makes the application easier to maintain and extend.

---

#### **4.2 Historical Data Management**
Sensor readings are managed through the nested endpoint /sensors/{sensorId}/readings.

The GET method retrieves historical readings for a sensor, while the POST method allows new readings to be added.

Each time a new reading is added, the currentValue field of the parent sensor is updated to reflect the most recent measurement. This ensures consistency between the sensor’s current state and its historical data.

---

### **Part 5 — Advanced Error Handling, Exception Mapping & Logging**

#### **5.1 Resource Conflict (409)**
HTTP 409 Conflict is used when a request cannot be completed due to a conflict with the current state of the resource.

In this system, attempting to delete a room that still has sensors assigned violates a business rule. Therefore, returning a 409 Conflict response is appropriate.

---

#### **5.2 Dependency Validation (422 Unprocessable Entity)**
HTTP 404 Not Found indicates that the requested endpoint does not exist.

HTTP 422 Unprocessable Entity is more appropriate when the request is valid but contains invalid data, such as a non-existent roomId in a valid POST request.

In this case, the endpoint exists, but the data is semantically incorrect, making 422 the better choice.

---

#### **5.3 State Constraint (403 Forbidden) **
HTTP 403 Forbidden is used when the server understands the request but refuses to perform it due to a restriction.

If a sensor is in MAINTENANCE state, it cannot accept new readings. Although the request is valid, the action is not allowed, so a 403 response is appropriate.

---

#### **5.4 The Global Safety Net (500) **
Exposing internal stack traces can reveal sensitive information such as class names, file structures, method details, and library versions.

This information can be exploited by attackers to identify vulnerabilities within the system.

To prevent this, a global exception handler should return a generic error response to clients while logging detailed error information internally.

---

#### **5.5 API Request & Response Logging Filters**
Using JAX-RS filters such as ContainerRequestFilter and ContainerResponseFilter is a better approach for logging compared to inserting logging statements within each resource method.

Filters allow centralized logging of all incoming requests and outgoing responses, ensuring consistency and reducing code duplication.

This approach improves maintainability and follows best practices for handling cross-cutting concerns in API design.
