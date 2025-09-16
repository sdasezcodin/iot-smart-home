# IoT SmartHome Dashboard - Entity Relationship Diagram

## Project Overview
This project is a Java-based IoT SmartHome Dashboard system that uses DynamoDB as its database backend. The system manages smart appliances and their sensor data using various design patterns including Factory, Observer, Command, and DAO patterns.

## Database Schema Analysis

### Core Entities

#### 1. Device Table (Appliance Entity)
**Table Name**: `Device`
**Primary Key**: `id` (Partition Key - String)

| Attribute | Type | Description |
|-----------|------|-------------|
| id | String (PK) | Unique identifier for the appliance |
| type | String | Type of appliance (AC, FAN, SPEAKER) |
| name | String | User-defined name for the appliance |
| brand | String | Brand of the appliance (Haier, LG, Sony) |
| on | Boolean | Power state (true=ON, false=OFF) |
| online | Boolean | Network connection state |
| level | Integer | Operational level (temperature, speed, volume) |
| powerUsage | Integer | Current power consumption in Watts |

#### 2. SensorData Table
**Table Name**: `SensorData`
**Composite Primary Key**: 
- `deviceId` (Partition Key - String)
- `id` (Sort Key - String)

| Attribute | Type | Description |
|-----------|------|-------------|
| id | String (SK) | Unique identifier for the sensor reading |
| deviceId | String (PK) | Foreign Key to Device.id |
| date | String | Date of reading (YYYY-MM-DD format) |
| time | String | Time of reading (HH:MM:SS format) |
| data | String | Actual sensor reading data/message |

## Entity Relationship Diagram (Mermaid Format)

```mermaid
erDiagram
    DEVICE {
        string id PK "Unique device identifier"
        string type "AC, FAN, SPEAKER"
        string name "User-defined name"
        string brand "Haier, LG, Sony"
        boolean on "Power state"
        boolean online "Network connection state"
        int level "Operational level"
        int powerUsage "Power consumption in Watts"
    }
    
    SENSOR_DATA {
        string id SK "Reading identifier"
        string deviceId PK,FK "Device reference"
        string date "Reading date YYYY-MM-DD"
        string time "Reading time HH:MM:SS"
        string data "Sensor reading message"
    }
    
    APPLIANCE_FACTORY {
        string factoryType "HaierFactory, LGFactory, SonyFactory"
        string supportedTypes "AC, FAN, SPEAKER"
    }
    
    DEVICE_OBSERVER {
        string observerId "Observer instance ID"
        string observerType "DeviceObserver"
    }
    
    NETWORK_CLIENT {
        string clientId "Network client identifier"
        string serverEndpoint "Server connection endpoint"
        boolean connected "Connection state"
    }
    
    SMART_HOME_SERVICE {
        string serviceId "Service layer identifier"
        map memoryDevices "In-memory device cache"
        boolean streamingActive "Sensor streaming state"
    }

    %% Relationships
    DEVICE ||--o{ SENSOR_DATA : "generates"
    APPLIANCE_FACTORY ||--o{ DEVICE : "creates"
    DEVICE ||--o{ DEVICE_OBSERVER : "notifies"
    DEVICE ||--o{ NETWORK_CLIENT : "connects_through"
    SMART_HOME_SERVICE ||--o{ DEVICE : "manages"
    SMART_HOME_SERVICE ||--o{ SENSOR_DATA : "processes"
```

## Detailed Relationship Analysis

### 1. Device to SensorData (One-to-Many)
- **Cardinality**: 1:N
- **Relationship**: One device can generate multiple sensor readings over time
- **Foreign Key**: `deviceId` in SensorData references `id` in Device
- **Query Pattern**: Efficiently query recent readings by device using composite key

### 2. Factory Pattern Relationships
- **ApplianceFactory**: Abstract factory creating different appliance types
- **Concrete Factories**: HaierFactory, LGFactory, SonyFactory
- **Products**: All create Appliance instances with brand-specific configurations

### 3. Observer Pattern Relationships
- **Subject**: Appliance implements Subject interface
- **Observer**: DeviceObserver implements Observer interface
- **Relationship**: Many-to-Many (one device can have multiple observers, one observer can watch multiple devices)

### 4. Service Layer Relationships
- **SmartHomeService**: Central orchestrator managing all components
- **Manages**: In-memory device cache, database operations, network communication
- **Dependencies**: ApplianceDAO, SensorDataDAO, Client, DeviceObserver

## Data Flow Architecture

### 1. Device Registration Flow
```
User Input → SmartHomeService → ApplianceFactory → Appliance → ApplianceDAO → DynamoDB Device Table
```

### 2. Sensor Data Flow
```
Device State Change → SensorDataGenerator → SensorData → SensorDataDAO → DynamoDB SensorData Table
```

### 3. Observer Notification Flow
```
Device State Change → Subject.notifyObservers() → DeviceObserver.update() → Console Output
```

### 4. Network Communication Flow
```
SmartHomeService → Client → Server → Network Topology Graph
```

## Design Patterns Implemented

### 1. Factory Pattern
- **Abstract Factory**: ApplianceFactory
- **Concrete Factories**: HaierFactory, LGFactory, SonyFactory
- **Products**: AC, Fan, Speaker appliances with brand-specific characteristics

### 2. Observer Pattern
- **Subject**: Appliance class
- **Observer**: DeviceObserver class
- **Purpose**: Real-time dashboard updates when device state changes

### 3. Command Pattern
- **Command**: Toggle, Simulate commands
- **Invoker**: Remote class
- **Receiver**: Appliance objects

### 4. Builder Pattern
- **Builder**: Appliance.Builder, SensorData.Builder
- **Purpose**: Flexible object creation with multiple optional parameters

### 5. Singleton Pattern
- **Implementation**: DynamoDBConnection
- **Purpose**: Single database connection instance throughout application lifecycle

### 6. DAO Pattern
- **Interfaces**: ApplianceDAO, SensorDataDAO
- **Implementations**: ApplianceDB, SensorDataDB
- **Purpose**: Data access layer abstraction

## Database Technology Stack

- **Primary Database**: Amazon DynamoDB (Local instance at localhost:8000)
- **Access Pattern**: AWS SDK v2 Enhanced Client
- **Key Strategy**: 
  - Device table uses simple partition key (id)
  - SensorData uses composite key (deviceId + id) for efficient time-series queries
- **Billing Mode**: Pay-per-request for development flexibility

## Performance Considerations

### 1. Query Optimization
- **Device Queries**: Simple key-based lookups using partition key
- **Sensor Data Queries**: Composite key enables efficient range queries by device and time
- **Limitation**: Cross-device date range queries require full table scan (inefficient)

### 2. Memory Management
- **In-Memory Cache**: ConcurrentHashMap for thread-safe device storage
- **Observer List**: CopyOnWriteArrayList for thread-safe observer notifications
- **Streaming**: Atomic operations for thread-safe streaming control

### 3. Scaling Recommendations
- **Add GSI**: Global Secondary Index on SensorData for efficient date-range queries
- **Connection Pooling**: Implement connection pooling for high-throughput scenarios
- **Batch Operations**: Use batch writes for bulk sensor data ingestion

## Security Considerations

- **Local Development**: Uses dummy AWS credentials for local DynamoDB
- **Production Ready**: Would need proper IAM roles and credentials management
- **Data Validation**: InputValidator class provides input sanitization
- **Exception Handling**: Custom exception hierarchy for proper error management

This ER diagram represents a well-architected IoT system with clear separation of concerns, proper use of design patterns, and efficient database design for time-series data storage and retrieval.