package com.smartcampus;

import com.smartcampus.exceptions.GlobalExceptionMapper;
import com.smartcampus.exceptions.SensorUnavailableExceptionMapper;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/v1")
public class SmartCampusApp extends Application {
}