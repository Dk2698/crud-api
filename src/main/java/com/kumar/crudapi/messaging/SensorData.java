package com.kumar.crudapi.messaging;

import lombok.Data;

@Data
public class SensorData {
    private String sensorId;
    private String sensorName;
    private String sensorType;
    private double sensorValue;
    private long sensorTimestamp;
}
