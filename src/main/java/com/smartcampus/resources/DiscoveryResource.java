package com.smartcampus.resources;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {
        Map<String, Object> info = new HashMap<>();
        
        // API version info
        info.put("version", "1.0");
        info.put("name", "Smart Campus API");
        info.put("contact", "admin@smartcampus.com");
        
        // Links to main resources (this is the HATEOAS part)
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        info.put("resources", links);
        
        return Response.ok(info).build();
    }
}