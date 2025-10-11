package com.smarthome.db;

import com.smarthome.exception.DatabaseException;
import com.smarthome.model.SensorData;
import com.smarthome.config.DynamoDBConnection;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DynamoDB-backed implementation of {@link SensorDataDAO}.
 * <p>
 * This class provides efficient operations for {@link SensorData} using DynamoDB.
 * <p>
 * Key features:
 * 1. Supports saving sensor data per device.
 * 2. Retrieves recent readings per device with limit.
 * 3. Retrieves readings by a date range (across all devices) with top 500 results,
 *    sorted by date then time (earliest first).
 * <p>
 * Note: Date range queries currently perform a full table scan.
 * For large datasets, consider using a Global Secondary Index (GSI) on date/time.
 */
public class SensorDataDB implements SensorDataDAO<SensorData> {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<SensorData> table;
    private static boolean tableChecked = false;
    private static final String TABLE_NAME = "SensorData";

    /**
     * Initializes the DynamoDB enhanced client and ensures the table exists.
     */
    public SensorDataDB() {
        try {
            this.enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(DynamoDBConnection.getInstance())
                    .build();

            ensureTableExists();

            this.table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(SensorData.class));
        } catch (DatabaseException e) {
            System.err.println("Cannot connect to DynamoDB. Database features will be unavailable.");
            throw e;
        }
    }

    /**
     * Checks if the DynamoDB table exists, creates it if not.
     * Synchronized to ensure this check happens only once.
     */
    private synchronized void ensureTableExists() {
        if (tableChecked) return;

        DynamoDbClient client = DynamoDBConnection.getInstance();

        try {
            client.describeTable(b -> b.tableName(TABLE_NAME));
        } catch (ResourceNotFoundException e) {
            try {
                client.createTable(CreateTableRequest.builder()
                        .tableName(TABLE_NAME)
                        .attributeDefinitions(
                                AttributeDefinition.builder()
                                        .attributeName("deviceId")
                                        .attributeType(ScalarAttributeType.S)
                                        .build(),
                                AttributeDefinition.builder()
                                        .attributeName("id")
                                        .attributeType(ScalarAttributeType.S)
                                        .build())
                        .keySchema(
                                KeySchemaElement.builder()
                                        .attributeName("deviceId")
                                        .keyType(KeyType.HASH)
                                        .build(),
                                KeySchemaElement.builder()
                                        .attributeName("id")
                                        .keyType(KeyType.RANGE)
                                        .build())
                        .billingMode(BillingMode.PAY_PER_REQUEST)
                        .build());

                client.waiter().waitUntilTableExists(r -> r.tableName(TABLE_NAME));
            } catch (Exception ex) {
                throw new DatabaseException("Failed to create table: " + TABLE_NAME, ex);
            }
        } catch (Exception e) {
            throw new DatabaseException("Error checking table existence: " + TABLE_NAME, e);
        }

        tableChecked = true;
    }

    /**
     * Saves a {@link SensorData} entity to the DynamoDB table.
     *
     * @param entity Sensor data to save.
     */
    @Override
    public void save(SensorData entity) {
        try {
            table.putItem(entity);
        } catch (Exception e) {
            throw new DatabaseException(
                    "Failed to save sensor reading with ID: " + entity.getId() + " for device: " + entity.getDeviceId(), e);
        }
    }

    /**
     * Retrieves recent sensor readings for a specific device.
     *
     * @param deviceId The device ID to fetch readings for.
     * @param limit    Maximum number of readings to return.
     * @return List of {@link SensorData} sorted descending by timestamp (latest first).
     */
    @Override
    public List<SensorData> findByDeviceId(String deviceId, int limit) {
        try {
            QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(deviceId).build());

            List<SensorData> result = new ArrayList<>();
            // Iterate and stop after reaching limit
            for (SensorData data : table.query(r -> r.queryConditional(query).scanIndexForward(false)).items()) {
                result.add(data);
                if (result.size() >= limit) break;  // Stop once we have enough
            }

            return result;
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch readings for device: " + deviceId, e);
        }
    }


    /**
     * Retrieves sensor readings across all devices for a given date range.
     * <p>
     * The results are:
     * - Sorted by date, then time ascending (earliest first)
     * - Limited to the top 500 readings
     *
     * @param start Start date in YYYY-MM-DD format
     * @param end   End date in YYYY-MM-DD format
     * @return List of {@link SensorData} matching the date range
     */
    @Override
    public List<SensorData> findByDateRange(String start, String end) {
        try {
            List<SensorData> result = new ArrayList<>();

            // Full day coverage
            String startFull = start + "T00:00:00";
            String endFull = end + "T23:59:59";

            // Scan all items (full table scan)
            for (SensorData r : table.scan().items()) {
                String ts = r.getDate() + "T" + r.getTime(); // e.g., 2025-10-11T14:23:05

                if (ts.compareTo(startFull) >= 0 && ts.compareTo(endFull) <= 0) {
                    result.add(r);
                }
            }

            // Sort ascending: earliest date/time first
            result.sort((a, b) -> (a.getDate() + "T" + a.getTime())
                    .compareTo(b.getDate() + "T" + b.getTime()));

            // Limit to top 500
            if (result.size() > 100) {
                return result.subList(0, 100);
            }

            return result;

        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch readings between " + start + " and " + end, e);
        }
    }

}
