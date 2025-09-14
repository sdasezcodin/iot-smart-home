package com.smarthome.db;

import com.smarthome.exception.DatabaseException;
import com.smarthome.model.SensorData;
import com.smarthome.config.DynamoDBConnection;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DynamoDB-backed implementation of {@link SensorDataDAO}.
 * <p>
 * This class provides efficient operations for time-series {@link SensorData}
 * entities using the AWS SDK enhanced client. It leverages a composite primary key
 * (deviceId as Partition Key and id as Sort Key) to enable fast and targeted queries.
 */
public class SensorDataDB implements SensorDataDAO<SensorData> {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<SensorData> table;
    private static boolean tableChecked = false;
    private static final String TABLE_NAME = "SensorData";

    /**
     * Initializes the DynamoDB enhanced client and ensures the
     * underlying table exists before accessing it.
     */
    public SensorDataDB() {
        try {
            this.enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(DynamoDBConnection.getInstance())
                    .build();

            // Ensure the DynamoDB table is created with the correct key schema.
            ensureTableExists();

            this.table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(SensorData.class));
        } catch (DatabaseException e) {
            System.err.println("Cannot connect to DynamoDB. Database features will be unavailable.");
            throw e;
        }
    }

    /**
     * Verifies the DynamoDB table exists and creates it if missing.
     * This check is synchronized and performed only once per application lifecycle.
     */
    private synchronized void ensureTableExists() {
        if (tableChecked) return;

        DynamoDbClient client = DynamoDBConnection.getInstance();

        try {
            // Attempt to describe the table to check for its existence.
            client.describeTable(b -> b.tableName(TABLE_NAME));
        } catch (ResourceNotFoundException e) {
            // If the table is not found, create it with the composite primary key.
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
                                        .keyType(KeyType.HASH) // Partition Key
                                        .build(),
                                KeySchemaElement.builder()
                                        .attributeName("id")
                                        .keyType(KeyType.RANGE) // Sort Key
                                        .build())
                        .billingMode(BillingMode.PAY_PER_REQUEST)
                        .build());

                // Wait for the table to become active before proceeding.
                client.waiter().waitUntilTableExists(r -> r.tableName(TABLE_NAME));
            } catch (Exception ex) {
                throw new DatabaseException("Failed to create table: " + TABLE_NAME, ex);
            }
        } catch (Exception e) {
            // Catch any other exceptions during the describe operation.
            throw new DatabaseException("Error checking table existence: " + TABLE_NAME, e);
        }

        tableChecked = true;
    }

    /** {@inheritDoc} */
    @Override
    public void save(SensorData entity) {
        try {
            table.putItem(entity);
        } catch (Exception e) {
            throw new DatabaseException("Failed to save sensor reading with ID: " + entity.getId(), e);
        }
    }

    /**
     * Retrieves the most recent readings for a specific device using a query.
     * This is much more efficient than a scan as it targets a specific partition.
     * {@inheritDoc}
     */
    @Override
    public List<SensorData> findByDeviceId(String deviceId, int limit) {
        try {
            // Define a query on the partition key (deviceId).
            QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(deviceId).build());

            List<SensorData> result = new ArrayList<>();
            // Execute the query and collect the results up to the specified limit.
            table.query(r -> r.queryConditional(query).limit(limit).scanIndexForward(false)) // scanIndexForward=false sorts descending
                    .items().forEach(result::add);

            return result;
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch readings for device: " + deviceId, e);
        }
    }

    /**
     * Retrieves all readings within a given date range using a query.
     * This leverages the sort key (id) to efficiently filter results within a partition.
     * NOTE: This method still requires a full scan if querying across all devices,
     * but it's kept as-is to handle the date range functionality as previously designed.
     * {@inheritDoc}
     */
    @Override
    public List<SensorData> findByDateRange(String start, String end) {
        // The previous implementation used a full scan, which is inefficient.
        // A better design would be to also include deviceId in the method signature to use a Query,
        // but to maintain compatibility with the interface, the scan is left in place.
        try {
            List<SensorData> result = new ArrayList<>();
            // Use a full table scan as there's no way to query by date range across all partitions
            // without a secondary index, which is not defined.
            for (SensorData r : table.scan().items()) {
                String ts = r.getDate() + "T" + r.getTime();
                if (ts.compareTo(start) >= 0 && ts.compareTo(end) <= 0) {
                    result.add(r);
                }
            }
            return result;
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch readings between " + start + " and " + end, e);
        }
    }

    /**
     * Retrieves the most recent readings across all devices using a scan.
     * NOTE: This operation is inefficient on large tables. A Global Secondary Index
     * (GSI) would be needed for an optimized solution.
     * {@inheritDoc}
     */
    @Override
    public List<SensorData> findRecent(int limit) {
        try {
            List<SensorData> result = new ArrayList<>();
            // A full table scan is performed to get all items.
            table.scan(ScanEnhancedRequest.builder().limit(limit).build()).items().forEach(result::add);

            // Sort the results in memory, which is inefficient for large datasets.
            result.sort((r1, r2) -> (r2.getDate() + "T" + r2.getTime())
                    .compareTo(r1.getDate() + "T" + r1.getTime()));

            if (result.size() > limit) {
                result = result.subList(0, limit);
            }
            return result;
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch recent readings", e);
        }
    }
}