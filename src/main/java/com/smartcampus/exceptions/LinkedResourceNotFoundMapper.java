package com.smartcampus.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException e) {
        return Response.status(422)
            .type(MediaType.APPLICATION_JSON)
            .entity("{\"error\":\"" + e.getMessage() + "\"}")
            .build();
    }
}