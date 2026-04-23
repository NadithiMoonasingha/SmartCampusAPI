package com.smartcampus.resources;

import com.smartcampus.DataStore;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;

@Path("/sensors")
public class SensorResource {

    // GET /api/v1/sensors — get all sensors, optional ?type= filter
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> result = new ArrayList<>();
        for (Sensor s : DataStore.sensors.values()) {
            if (type == null || s.getType().equalsIgnoreCase(type)) {
                result.add(s);
            }
        }
        return Response.ok(result).build();
    }

    // POST /api/v1/sensors — register a new sensor
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        // Validate sensor ID
        if (sensor.getId() == null || sensor.getId().isEmpty()) {
            return Response.status(400).entity("{\"error\":\"Sensor ID is required\"}").build();
        }
        // Check sensor doesn't already exist
        if (DataStore.sensors.containsKey(sensor.getId())) {
            return Response.status(409).entity("{\"error\":\"Sensor already exists\"}").build();
        }
        // Validate that the roomId exists
        if (sensor.getRoomId() == null || !DataStore.rooms.containsKey(sensor.getRoomId())) {
            return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\":\"Room not found — the specified roomId does not exist\"}")
                .build();
        }
        // Save sensor
        DataStore.sensors.put(sensor.getId(), sensor);
        // Also add sensor ID to the room's sensorIds list
        Room room = DataStore.rooms.get(sensor.getRoomId());
        room.getSensorIds().add(sensor.getId());
        // Initialize empty readings list for this sensor
        DataStore.readings.put(sensor.getId(), new ArrayList<>());
        return Response.status(201).entity(sensor).build();
    }

    // GET /api/v1/sensors/{sensorId} — get one sensor
    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(404).entity("{\"error\":\"Sensor not found\"}").build();
        }
        return Response.ok(sensor).build();
    }

    // Sub-resource locator for readings
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}