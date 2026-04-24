package com.smartcampus.resources;

import com.smartcampus.DataStore;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.smartcampus.exceptions.SensorUnavailableException;

public class SensorReadingResource {

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            return Response.status(404)
                    .entity("{\"error\":\"Sensor not found\"}")
                    .build();
        }

        List<SensorReading> readings = DataStore.readings.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    // POST /api/v1/sensors/{sensorId}/readings
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(404).entity("{\"error\":\"Sensor not found\"}").build();
        }
        // Block if sensor is in MAINTENANCE
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            return Response.status(403)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\":\"Sensor is under maintenance and cannot accept readings\"}")
                .build();
        }
        // Generate ID and timestamp if not provided
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }
        // Save reading — use computeIfAbsent to avoid NPE
        DataStore.readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
        // Update sensor's currentValue
        sensor.setCurrentValue(reading.getValue());
        return Response.status(201).entity(reading).build();
    }
}