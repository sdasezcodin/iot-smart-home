package com.smarthome.db;

import com.smarthome.exception.DatabaseException;
import com.smarthome.config.DynamoDBConnection;
import com.smarthome.model.Appliance;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DynamoDB-backed implementation of {@link ApplianceDAO}.
 * <p>
 * Provides CRUD operations for {@link Appliance} entities
 * using the AWS SDK enhanced client.
 */
public class ApplianceDB implements ApplianceDAO<Appliance> {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Appliance> table;
    private static boolean tableChecked = false;
    private static final String TABLE_NAME = "Device";

    /**
     * Initializes the DynamoDB client and ensures the
     * underlying table exists.
     */
    public ApplianceDB() {
        DynamoDbClient client;

        try {
            client = DynamoDBConnection.getInstance();

            this.enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(client)
                    .build();

            ensureTableExists();

            this.table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Appliance.class));

        } catch (Exception e) {
            System.err.println("Cannot connect to DynamoDB at localhost:8000. Make sure DynamoDB Local is running.");
            System.err.println("Database features will be unavailable.");
            throw new DatabaseException("DynamoDB connection failed.", e);
        }
    }

    /**
     * Checks whether the DynamoDB table exists and creates it if not.
     * Executed only once per application lifecycle.
     */
    private synchronized void ensureTableExists() {
        if (tableChecked) return;

        DynamoDbClient client = DynamoDBConnection.getInstance();
        if (client == null) return; // skip if no connection

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
    public void save(Appliance entity) {
        try {
            table.putItem(entity);
        } catch (Exception e) {
            throw new DatabaseException("Failed to save device with ID: " + entity.getId(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void update(Appliance entity) {
        try {
            table.updateItem(entity);
        } catch (Exception e) {
            throw new DatabaseException("Failed to update device with ID: " + entity.getId(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Appliance findById(String id) {
        try {
            Appliance appliance = table.getItem(r -> r.key(k -> k.partitionValue(id)));
            if (appliance == null) {
                throw new DatabaseException("Device not found with ID: " + id);
            }
            return appliance;
        } catch (Exception e) {
            throw new DatabaseException("Error fetching device with ID: " + id, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Appliance> findAll() {
        try {
            List<Appliance> list = new ArrayList<>();
            table.scan().items().forEach(list::add);
            return list;
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch all devices", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteById(String id) {
        try {
            table.deleteItem(r -> r.key(k -> k.partitionValue(id)));
        } catch (Exception e) {
            throw new DatabaseException("Failed to delete device with ID: " + id, e);
        }
    }
}
