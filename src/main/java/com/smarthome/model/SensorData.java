package com.smarthome.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Represents a sensor reading from a smart home device.
 * <p>
 * This class serves as the data model for time-series sensor data, such as
 * temperature, humidity, or power usage. It is designed to be stored in
 * a DynamoDB table named "SensorData". The class leverages a composite
 * primary key for efficient data retrieval:
 * <ul>
 * <li><b>Partition Key:</b> {@code deviceId} to group all readings from a single device.</li>
 * <li><b>Sort Key:</b> {@code id} to order readings chronologically within a device's partition.</li>
 * </ul>
 */
@DynamoDbBean
public class SensorData {

    private String id;
    private String deviceId;
    private String date;   // format: YYYY-MM-DD
    private String time;   // format: HH:MM:SS
    private String data;   // sensor reading value

    // -------- Constructors --------
    /**
     * Default constructor.
     * Required by the DynamoDB Enhanced Client to instantiate the object.
     */
    public SensorData() {
        // Required by DynamoDB mapping
    }

    /**
     * Private constructor used by the Builder pattern to create an immutable SensorData object.
     * @param builder The builder instance containing the sensor reading properties.
     */
    private SensorData(Builder builder) {
        this.id = builder.id;
        this.deviceId = builder.deviceId;
        this.date = builder.date;
        this.time = builder.time;
        this.data = builder.data;
    }

    // -------- DynamoDB mapping Getters and Setters --------

    /**
     * Retrieves the unique identifier of the device that generated the reading.
     * This field serves as the DynamoDB Partition Key, grouping all readings
     * from the same device together for fast lookups.
     * @return The device ID.
     */
    @DynamoDbPartitionKey // <-- ADDED THIS ANNOTATION
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    /**
     * Retrieves the unique identifier for the sensor reading.
     * This field serves as the DynamoDB Sort Key, allowing readings
     * to be sorted chronologically for efficient range queries.
     * @return The unique reading ID.
     */
    @DynamoDbSortKey
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    /**
     * Retrieves the date of the sensor reading in "YYYY-MM-DD" format.
     * @return The date string.
     */
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    /**
     * Retrieves the time of the sensor reading in "HH:MM:SS" format.
     * @return The time string.
     */
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    /**
     * Retrieves the actual sensor reading value.
     * @return The sensor data value.
     */
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    // -------- Builder --------
    /**
     * A static nested class that implements the Builder pattern for creating
     * {@link SensorData} instances in a clear and readable manner.
     */
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

        /**
         * Builds and returns a new {@link SensorData} instance with the specified properties.
         * @return A new SensorData object.
         */
        public SensorData build() {
            return new SensorData(this);
        }
    }

    // -------- Utility --------
    /**
     * Returns a string representation of the SensorData object
     * @return A formatted string displaying all properties of the reading.
     */
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