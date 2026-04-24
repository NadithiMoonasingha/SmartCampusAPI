package com.smartcampus.exceptions;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        System.out.println("403 MAPPER WORKING");

        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .build();
    }
}