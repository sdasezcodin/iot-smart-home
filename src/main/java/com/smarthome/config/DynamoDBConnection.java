package com.smarthome.config;

import com.smarthome.exception.DatabaseException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

public class DynamoDBConnection {

    private static DynamoDbClient dynamoDb;

    private DynamoDBConnection() {}

    public static synchronized DynamoDbClient getInstance() throws DatabaseException {
        if (dynamoDb == null) {
            try {
                dynamoDb = DynamoDbClient.builder()
                        .endpointOverride(URI.create("http://localhost:8000"))
                        .region(Region.AP_SOUTH_1)
                        .credentialsProvider(StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("dummy", "dummy")
                        ))
                        .build();
            } catch (Exception e) {
                throw new DatabaseException("Failed to initialize DynamoDB client", e);
            }
        }
        return dynamoDb;
    }

    public static synchronized void closeConnection() throws DatabaseException {
        if (dynamoDb != null) {
            try {
                dynamoDb.close();
            } catch (Exception e) {
                throw new DatabaseException("Failed to close DynamoDB connection", e);
            } finally {
                dynamoDb = null;
            }
        }
    }
}
