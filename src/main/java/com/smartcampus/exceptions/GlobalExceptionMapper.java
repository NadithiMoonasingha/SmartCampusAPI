package com.smartcampus.exceptions;

import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger logger = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable e) {
        logger.severe("Unexpected error: " + e.getMessage());
        return Response.status(500)
            .type(MediaType.APPLICATION_JSON)
            .entity("{\"error\":\"Internal server error. Please contact support.\"}")
            .build();
    }
}