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
 * DynamoDB-backed implementation of the {@link ApplianceDAO} interface.
 * <p>
 * This class provides a concrete implementation of CRUD (Create, Read, Update, Delete)
 * operations for {@link Appliance} entities, using the AWS SDK for Java's
 * enhanced DynamoDB client. It manages the connection to a DynamoDB table
 * named "Device".
 */
public class ApplianceDB implements ApplianceDAO<Appliance> {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Appliance> table;
    private static boolean tableChecked = false;
    private static final String TABLE_NAME = "Device";

    /**
     * Initializes the DynamoDB client and ensures the "Device" table exists.
     * This constructor attempts to establish a connection to the local DynamoDB instance.
     *
     * @throws DatabaseException if the connection to DynamoDB fails.
     */
    public ApplianceDB() {
        DynamoDbClient client;

        try {
            // Get the singleton instance of the DynamoDB client
            client = DynamoDBConnection.getInstance();

            // Build the enhanced client, which provides a higher-level, object-oriented interface.
            this.enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(client)
                    .build();

            // Ensure the DynamoDB table is created before any operations are performed.
            ensureTableExists();

            // Get a reference to the table, mapped to the Appliance class.
            this.table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Appliance.class));

        } catch (Exception e) {
            // Provide a user-friendly message if the connection fails, as this is a common issue.
            System.err.println("Cannot connect to DynamoDB at localhost:8000. Make sure DynamoDB Local is running.");
            System.err.println("Database features will be unavailable.");
            // Re-throw as a custom exception to be handled by the calling layer.
            throw new DatabaseException("DynamoDB connection failed.", e);
        }
    }

    /**
     * A helper method to check if the DynamoDB table exists and create it if it doesn't.
     * This check is performed only once per application run to avoid unnecessary API calls.
     *
     * @throws DatabaseException if there's an error checking or creating the table.
     */
    private synchronized void ensureTableExists() {
        // Use a static flag to ensure this expensive check runs only once.
        if (tableChecked) return;

        DynamoDbClient client = DynamoDBConnection.getInstance();
        // Return immediately if the client is not available.
        if (client == null) return;

        try {
            // Attempt to describe the table. If it exists, this call will succeed.
            client.describeTable(b -> b.tableName(TABLE_NAME));
        } catch (ResourceNotFoundException e) {
            // If the table does not exist, a ResourceNotFoundException is thrown,
            // and we proceed to create the table.
            try {
                client.createTable(CreateTableRequest.builder()
                        .tableName(TABLE_NAME)
                        .attributeDefinitions(AttributeDefinition.builder()
                                .attributeName("id")
                                .attributeType(ScalarAttributeType.S)
                                .build())
                        .keySchema(KeySchemaElement.builder()
                                .attributeName("id")
                                .keyType(KeyType.HASH) // HASH specifies this as the partition key.
                                .build())
                        .billingMode(BillingMode.PAY_PER_REQUEST)
                        .build());
            } catch (Exception ex) {
                // Catch any creation errors and wrap them in a custom exception.
                throw new DatabaseException("Failed to create table: " + TABLE_NAME, ex);
            }
        } catch (Exception e) {
            // Catch other general errors during the describeTable call.
            throw new DatabaseException("Error checking table existence: " + TABLE_NAME, e);
        }

        tableChecked = true;
    }

    /**
     * Saves a new {@link Appliance} entity to the database.
     * {@inheritDoc}
     */
    @Override
    public void save(Appliance entity) {
        try {
            // The putItem method handles both create and update operations based on the key.
            table.putItem(entity);
        } catch (Exception e) {
            throw new DatabaseException("Failed to save device with ID: " + entity.getId(), e);
        }
    }

    /**
     * Updates an existing {@link Appliance} entity in the database.
     * {@inheritDoc}
     */
    @Override
    public void update(Appliance entity) {
        try {
            // updateItem is used for updating specific attributes of an item.
            table.updateItem(entity);
        } catch (Exception e) {
            throw new DatabaseException("Failed to update device with ID: " + entity.getId(), e);
        }
    }

    /**
     * Finds a single {@link Appliance} by its unique ID.
     * {@inheritDoc}
     */
    @Override
    public Appliance findById(String id) {
        try {
            // getItem is an efficient way to retrieve a single item using its primary key.
            Appliance appliance = table.getItem(r -> r.key(k -> k.partitionValue(id)));
            if (appliance == null) {
                // Throw an exception if the item is not found, providing a clear error message.
                throw new DatabaseException("Device not found with ID: " + id);
            }
            return appliance;
        } catch (Exception e) {
            // Catch any errors during the retrieval process.
            throw new DatabaseException("Error fetching device with ID: " + id, e);
        }
    }

    /**
     * Retrieves all {@link Appliance} entities from the database.
     * {@inheritDoc}
     */
    @Override
    public List<Appliance> findAll() {
        try {
            List<Appliance> list = new ArrayList<>();
            // The scan method reads every item in the table. This should be used cautiously
            // in production due to high cost and potential for performance issues on large tables.
            table.scan().items().forEach(list::add);
            return list;
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch all devices", e);
        }
    }

    /**
     * Deletes a single {@link Appliance} from the database using its unique ID.
     * {@inheritDoc}
     */
    @Override
    public void deleteById(String id) {
        try {
            // deleteItem removes a record from the table based on its primary key.
            table.deleteItem(r -> r.key(k -> k.partitionValue(id)));
        } catch (Exception e) {
            throw new DatabaseException("Failed to delete device with ID: " + id, e);
        }
    }
}