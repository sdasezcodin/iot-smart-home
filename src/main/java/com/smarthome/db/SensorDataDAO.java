package com.smarthome.db;

import java.util.List;

/**
 * Generic Data Access Object (DAO) interface for accessing time-series sensor data.
 * <p>
 * This interface defines the contract for operations specifically tailored for
 * time-series data, such as sensor readings. It provides methods for saving data
 * and retrieving it based on device ID, date range, or recency.
 *
 * @param <T> The type of sensor reading entity managed by this DAO.
 */
public interface SensorDataDAO<T> {

    /**
     * Persists a new sensor reading in the database.
     * <p>
     * This method saves a single time-stamped sensor data point to the underlying
     * data store.
     *
     * @param reading The sensor reading object to save.
     */
    void save(T reading);

    /**
     * Retrieves the most recent readings for a specific device.
     * <p>
     * This is an efficient query that uses the device ID to locate and retrieve
     * a limited number of the most recent readings for that device.
     *
     * @param deviceId The unique identifier of the device.
     * @param limit    The maximum number of readings to fetch.
     * @return A {@link List} of recent readings for the given device, sorted by time.
     */
    List<T> findByDeviceId(String deviceId, int limit);

    /**
     * Retrieves all readings across devices within a given date range.
     * <p>
     * This method fetches all sensor readings that fall between the specified
     * start and end timestamps. The timestamps should be in a consistent format
     * (e.g., "YYYY-MM-DDTHH:MM:SS").
     *
     * @param start The start timestamp (inclusive).
     * @param end   The end timestamp (inclusive).
     * @return A {@link List} of all readings within the specified date range.
     */
    List<T> findByDateRange(String start, String end);

    /**
     * Retrieves the most recent readings across all devices
     * <p>
     * This operation fetches the latest readings from the entire collection of
     * sensor data, up to the specified limit.
     *
     * @param limit The maximum number of readings to fetch.
     * @return A {@link List} of the most recent readings across all devices,
     * sorted in descending order by time.
     */
    List<T> findRecent(int limit);
}