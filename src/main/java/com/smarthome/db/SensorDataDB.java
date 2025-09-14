package com.smarthome.db;

import com.smarthome.exception.DatabaseException;
import com.smarthome.model.SensorData;
import com.smarthome.config.DynamoDBConnection;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DynamoDB-backed implementation of {@link SensorDataDAO}.
 * <p>
 * Provides CRUD-like operations for time-series {@link SensorData}
 * entities using the AWS SDK enhanced client.
 */
public class SensorDataDB implements SensorDataDAO<SensorData> {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<SensorData> table;
    private static boolean tableChecked = false;
    private static final String TABLE_NAME = "SensorData";

    /**
     * Initializes the DynamoDB enhanced client and ensures
     * the underlying table exists before accessing it.
     */
    public SensorDataDB() {
        this.enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(DynamoDBConnection.getInstance())
                .build();

        ensureTableExists();

        this.table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(SensorData.class));
    }

    /**
     * Verifies the DynamoDB table exists, and creates it if missing.
     * <p>
     * Uses a one-time check to avoid redundant describe/create calls.
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
                        .attributeDefinitions(AttributeDefinition.builder()
                                .attributeName("id")
                                .attributeType(ScalarAttributeType.S)
                                .build())
                        .keySchema(KeySchemaElement.builder()
                                .attributeName("id")
                                .keyType(KeyType.HASH)
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

    /** {@inheritDoc} */
    @Override
    public void save(SensorData entity) {
        try {
            table.putItem(entity);
        } catch (Exception e) {
            throw new DatabaseException("Failed to save sensor reading with ID: " + entity.getId(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<SensorData> findByDeviceId(String deviceId, int limit) {
        try {
            List<SensorData> result = new ArrayList<>();
            for (SensorData r : table.scan().items()) {
                if (r.getDeviceId().equals(deviceId)) {
                    result.add(r);
                    if (result.size() >= limit) break;
                }
            }
            return result;
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch readings for device: " + deviceId, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<SensorData> findByDateRange(String start, String end) {
        try {
            List<SensorData> result = new ArrayList<>();
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

    /** {@inheritDoc} */
    @Override
    public List<SensorData> findRecent(int limit) {
        try {
            List<SensorData> result = new ArrayList<>();
            for (SensorData r : table.scan().items()) {
                result.add(r);
            }

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
