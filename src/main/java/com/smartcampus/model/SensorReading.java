package com.smartcampus.model;

public class SensorReading {
    private String id;
    private long timestamp;
    private double value;

    // Empty constructor (required for JSON)
    public SensorReading() {}

    // Constructor with all fields
    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    // Getters
    public String getId() { return id; }
    public long getTimestamp() { return timestamp; }
    public double getValue() { return value; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setValue(double value) { this.value = value; }
}