package com.smarthome.config;

import com.smarthome.exception.DatabaseException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

/**
 * Singleton class for managing the DynamoDB client connection.
 * <p>
 * This class ensures that only a single instance of the DynamoDbClient is
 * created and used throughout the application, which is a best practice for
 * managing resources and improving performance. It is designed to work with a
 * local DynamoDB instance.
 */
public class DynamoDBConnection {

    // The single, shared instance of the DynamoDB client.
    private static DynamoDbClient dynamoDb;

    /**
     * Private constructor to prevent direct instantiation.
     * This is a key part of the Singleton design pattern.
     */
    private DynamoDBConnection() {}

    /**
     * Provides a thread-safe way to get the singleton instance of the DynamoDbClient
     * If the client has not been initialized yet, it creates a new one.
     *
     * @return The single instance of DynamoDbClient.
     * @throws DatabaseException If the client fails to initialize.
     */
    public static synchronized DynamoDbClient getInstance() throws DatabaseException {
        // Check if the instance is null before creating it.
        if (dynamoDb == null) {
            try {
                // Build the DynamoDB client with specific settings for a local instance.
                dynamoDb = DynamoDbClient.builder()
                        .endpointOverride(URI.create("http://localhost:8000")) // Points to the local DynamoDB instance.
                        .region(Region.AP_SOUTH_1) // A standard region for development.
                        .credentialsProvider(StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("dummy", "dummy") // Dummy credentials for local testing.
                        ))
                        .build();
            } catch (Exception e) {
                // Wrap any exceptions in a custom DatabaseException for consistent error handling.
                throw new DatabaseException("Failed to initialize DynamoDB client", e);
            }
        }
        return dynamoDb;
    }

    /**
     * Closes the DynamoDB client connection and nullifies the instance.
     * This method is essential for a graceful shutdown of the application,
     * releasing resources and preventing resource leaks.
     *
     * @throws DatabaseException If the client fails to close.
     */
    public static synchronized void closeConnection() throws DatabaseException {
        if (dynamoDb != null) {
            try {
                dynamoDb.close(); // Close the client to release system resources.
            } catch (Exception e) {
                // Wrap any exceptions in a custom DatabaseException.
                throw new DatabaseException("Failed to close DynamoDB connection", e);
            } finally {
                // Set the instance to null to allow for a new connection if needed later.
                dynamoDb = null;
            }
        }
    }
}