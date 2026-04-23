package com.smartcampus.resources;

import com.smartcampus.DataStore;
import com.smartcampus.model.Room;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.smartcampus.exceptions.RoomNotEmptyException;

@Path("/rooms")
public class RoomResource {

    // GET /api/v1/rooms — return all rooms
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(DataStore.rooms.values());
        return Response.ok(roomList).build();
    }

    // POST /api/v1/rooms — create a new room
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(400).entity("{\"error\":\"Room ID is required\"}").build();
        }
        if (DataStore.rooms.containsKey(room.getId())) {
            return Response.status(409).entity("{\"error\":\"Room already exists\"}").build();
        }
        DataStore.rooms.put(room.getId(), room);
        return Response.status(201).entity(room).build();
    }

    // GET /api/v1/rooms/{roomId} — get one room
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            return Response.status(404).entity("{\"error\":\"Room not found\"}").build();
        }
        return Response.ok(room).build();
    }

    // DELETE /api/v1/rooms/{roomId} — delete a room
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            return Response.status(404)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\":\"Room not found\"}")
                .build();
        }
        if (!room.getSensorIds().isEmpty()) {
            return Response.status(409)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\":\"Cannot delete room — it still has sensors assigned to it\"}")
                .build();
        }
        DataStore.rooms.remove(roomId);
        return Response.ok("{\"message\":\"Room deleted successfully\"}").build();
    }
}