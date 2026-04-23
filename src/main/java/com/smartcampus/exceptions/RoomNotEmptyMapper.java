package com.smartcampus.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {
    @Override
    public Response toResponse(RoomNotEmptyException e) {
        return Response.status(409)
            .type(MediaType.APPLICATION_JSON)
            .entity("{\"error\":\"" + e.getMessage() + "\"}")
            .build();
    }
}