package com.smarthome.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * Represents a sensor reading from a device.
 * <p>
 * Stored in DynamoDB (table "SensorData") using {@link DynamoDbBean} mapping.
 * Each reading contains a timestamp, device ID, and the measured data.
 */
@DynamoDbBean
public class SensorData {

    private String id;
    private String deviceId;
    private String date;   // format: YYYY-MM-DD
    private String time;   // format: HH:MM:SS
    private String data;   // sensor reading value

    // -------- Constructors --------
    public SensorData() {
        // Required by DynamoDB mapping
    }

    private SensorData(Builder builder) {
        this.id = builder.id;
        this.deviceId = builder.deviceId;
        this.date = builder.date;
        this.time = builder.time;
        this.data = builder.data;
    }

    // -------- DynamoDB mapping --------
    @DynamoDbPartitionKey
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    // -------- Builder --------
    public static class Builder {
        private String id;
        private String deviceId;
        private String date;
        private String time;
        private String data;

        public Builder id(String id) { this.id = id; return this; }
        public Builder deviceId(String deviceId) { this.deviceId = deviceId; return this; }
        public Builder date(String date) { this.date = date; return this; }
        public Builder time(String time) { this.time = time; return this; }
        public Builder data(String data) { this.data = data; return this; }

        public SensorData build() {
            return new SensorData(this);
        }
    }

    // -------- Utility --------
    @Override
    public String toString() {
        return "SensorData{" +
                "id='" + id + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
