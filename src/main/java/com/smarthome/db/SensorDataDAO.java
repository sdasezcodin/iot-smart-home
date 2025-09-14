package com.smarthome.db;

import java.util.List;

/**
 * Generic DAO interface for accessing time-series sensor data.
 *
 * @param <T> the type of sensor reading entity managed by this DAO
 */
public interface SensorDataDAO<T> {

    /**
     * Persists a new sensor reading in the database.
     *
     * @param reading the sensor reading to save
     */
    void save(T reading);

    /**
     * Retrieves the most recent readings for a specific device.
     *
     * @param deviceId the unique identifier of the device
     * @param limit    the maximum number of readings to fetch
     * @return list of recent readings for the given device
     */
    List<T> findByDeviceId(String deviceId, int limit);

    /**
     * Retrieves all readings across devices within a given date range.
     *
     * @param start the start timestamp (inclusive)
     * @param end   the end timestamp (inclusive)
     * @return list of readings within the date range
     */
    List<T> findByDateRange(String start, String end);

    /**
     * Retrieves the most recent readings across all devices.
     *
     * @param limit the maximum number of readings to fetch
     * @return list of recent readings across devices
     */
    List<T> findRecent(int limit);
}
