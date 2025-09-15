# Design Document
# IoT Smart Home Dashboard

---

**Document Version**: 1.0  
**Date**: September 15, 2025  
**Project**: IoT Smart Home Dashboard  
**Technology Stack**: Java 21, Maven, AWS DynamoDB, Docker  

---

## Table of Contents

1. [System Architecture](#1-system-architecture)
2. [Design Patterns](#2-design-patterns)
3. [Component Design](#3-component-design)
4. [Database Design](#4-database-design)
5. [API Design](#5-api-design)
6. [User Interface Design](#6-user-interface-design)
7. [Data Flow Design](#7-data-flow-design)
8. [Security Design](#8-security-design)

---

## 1. System Architecture

### 1.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        IoT Smart Home Dashboard                 │
├─────────────────────────────────────────────────────────────────┤
│  Presentation Layer (View)                                      │
│  ┌─────────────────┐    ┌─────────────────┐                    │
│  │   ConsoleMenu   │    │ DeviceObserver  │                    │
│  │   (User Interface)   │  (Dashboard)    │                    │
│  └─────────────────┘    └─────────────────┘                    │
├─────────────────────────────────────────────────────────────────┤
│  Controller Layer                                               │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │           SmartHomeController                               │ │
│  │        (Input Handling & Orchestration)                    │ │
│  └─────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  Service Layer (Business Logic)                                │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │              SmartHomeService                               │ │
│  │    (Device Management, Sensor Streaming, Network)          │ │
│  └─────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  Model Layer                                                   │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │  Appliance  │  │ SensorData  │  │    Design Patterns      │ │
│  │   (Device)  │  │ (Readings)  │  │ Command, Factory, etc.  │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  Data Access Layer                                             │
│  ┌─────────────────┐    ┌─────────────────────────────────────┐ │
│  │  ApplianceDB    │    │        SensorDataDB                 │ │
│  │  (Device DAO)   │    │      (Sensor DAO)                  │ │
│  └─────────────────┘    └─────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  Infrastructure Layer                                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │  TCP Client │  │  TCP Server │  │     AWS DynamoDB        │ │
│  │  (Network)  │  │  (Network)  │  │     (Persistence)       │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 Architecture Patterns

- **MVC (Model-View-Controller)**: Clear separation of concerns
- **Layered Architecture**: Organized into distinct layers
- **Dependency Injection**: Components receive dependencies through constructors
- **Singleton**: AppConfig ensures single configuration instance

### 1.3 Key Components

| Component | Responsibility | Layer |
|-----------|----------------|-------|
| SmartHomeApp | Application entry point | Main |
| AppConfig | Configuration and initialization | Config |
| ConsoleMenu | User interface and menu system | View |
| SmartHomeController | Request handling and coordination | Controller |
| SmartHomeService | Business logic and operations | Service |
| Appliance | Device model and behavior | Model |
| ApplianceDB | Data persistence for devices | Data Access |
| Client/Server | Network communication | Infrastructure |

---

## 2. Design Patterns

### 2.1 Builder Pattern

**Location**: `Appliance.Builder`, `SensorData.Builder`

```java
// Usage Example
Appliance appliance = new Appliance.Builder()
    .id("AC001")
    .type("AC")
    .name("Living Room AC")
    .brand("Samsung")
    .level(25)
    .build();
```

**Benefits**:
- Flexible object construction
- Immutable objects
- Clear parameter specification

### 2.2 Factory Pattern

**Location**: `ApplianceFactory`, `HaierFactory`, `LGFactory`, `SonyFactory`

```java
// Abstract Factory Interface
public interface ApplianceFactory {
    Appliance.Builder acBuilder();
    Appliance.Builder fanBuilder();
    Appliance.Builder speakerBuilder();
}

// Concrete Factory
public class HaierFactory implements ApplianceFactory {
    @Override
    public Appliance.Builder acBuilder() {
        return new Appliance.Builder().brand("Haier").type("AC");
    }
}
```

**Benefits**:
- Easy addition of new brands
- Encapsulated object creation
- Brand-specific configurations

### 2.3 Command Pattern

**Location**: `Command`, `Toggle`, `Simulate`, `Remote`

```java
// Command Interface
public interface Command {
    void execute();
}

// Concrete Commands
public class Toggle implements Command {
    private final Appliance appliance;
    
    public Toggle(Appliance appliance) {
        this.appliance = appliance;
    }
    
    @Override
    public void execute() {
        appliance.toggleOnOff();
    }
}

// Invoker
public class Remote {
    private Command command;
    
    public void setCommand(Command command) {
        this.command = command;
    }
    
    public void pressButton() {
        command.execute();
    }
}
```

**Benefits**:
- Decoupled request from execution
- Easy command extension
- Potential undo/redo capability

### 2.4 Observer Pattern

**Location**: `Observer`, `Subject`, `DeviceObserver`

```java
// Subject Interface
public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(String message);
}

// Observer Interface
public interface Observer {
    void update(String message);
}

// Implementation in Appliance
public class Appliance implements Device, Subject {
    private final List<Observer> observers = new CopyOnWriteArrayList<>();
    
    @Override
    public void notifyObservers(String message) {
        for (Observer o : observers) o.update(message);
    }
}
```

**Benefits**:
- Real-time state notifications
- Loose coupling between components
- Multiple observers support

### 2.5 Singleton Pattern

**Location**: `AppConfig`

```java
public class AppConfig {
    private static AppConfig instance;
    
    private AppConfig() {
        // Initialize components
    }
    
    public static AppConfig init() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
}
```

**Benefits**:
- Single configuration instance
- Controlled resource initialization
- Global access point

---

## 3. Component Design

### 3.1 Core Models

#### 3.1.1 Appliance Class

```java
@DynamoDbBean
public class Appliance implements Device, Subject {
    // Properties
    private String id;           // Unique identifier
    private String type;         // AC, FAN, SPEAKER
    private String name;         // User-defined name
    private String brand;        // Haier, LG, Sony
    private boolean on;          // Power state
    private boolean online;      // Network state
    private int level;           // Operational level
    private int powerUsage;      // Power consumption (W)
    
    // Observer list for notifications
    private final List<Observer> observers = new CopyOnWriteArrayList<>();
    
    // Device interface methods
    public void toggleOnOff() { /* Implementation */ }
    public void simulate(int val) { /* Implementation */ }
    
    // Power calculation
    public int calculatePower(int level) {
        return switch (type.toUpperCase()) {
            case "AC" -> 50 + Math.max(0, (30 - level) * 80);
            case "FAN" -> switch (level) {
                case 1 -> 15; case 2 -> 25; case 3 -> 35;
                case 4 -> 45; case 5 -> 55; default -> 0;
            };
            case "SPEAKER" -> 5 + (int)(level * 0.5);
            default -> 0;
        };
    }
}
```

#### 3.1.2 SensorData Class

```java
@DynamoDbBean
public class SensorData {
    private String id;        // Unique reading ID
    private String deviceId;  // Associated device
    private String date;      // YYYY-MM-DD
    private String time;      // HH:mm:ss
    private String data;      // Sensor reading message
    
    // Builder pattern for construction
    public static class Builder { /* Implementation */ }
}
```

### 3.2 Service Components

#### 3.2.1 SmartHomeService

**Key Responsibilities**:
- Device lifecycle management (register/deregister)
- Command execution coordination
- Sensor data streaming
- Network topology management
- Database operations coordination

**Key Methods**:
```java
public class SmartHomeService {
    // Device Management
    public void registerDevice(String type, String brand, String name);
    public void deregisterDevice(String id);
    public List<Appliance> getAllDevices();
    
    // Device Control
    public void toggleDevice(String deviceId);
    public void simulateDevice(String deviceId, String levelInput);
    
    // Network Management
    public void connectDevice(String deviceId);
    public void disconnectDevice(String deviceId);
    public String buildNetworkTopology();
    
    // Sensor Operations
    public void startSensorStream();
    public void stopSensorStream();
    public List<SensorData> getReadingsByDevice(String deviceId, String limit);
    public List<SensorData> getReadingsByDateRange(String start, String end);
}
```

### 3.3 Data Access Components

#### 3.3.1 DAO Pattern Implementation

```java
// Generic DAO Interface
public interface ApplianceDAO<T> {
    void save(T entity);
    void update(T entity);
    T findById(String id);
    List<T> findAll();
    void deleteById(String id);
}

// DynamoDB Implementation
public class ApplianceDB implements ApplianceDAO<Appliance> {
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Appliance> table;
    
    // CRUD operations implementation
}
```

---

## 4. Database Design

### 4.1 DynamoDB Tables

#### 4.1.1 Device Table

| Attribute | Type | Key | Description |
|-----------|------|-----|-------------|
| id | String | Partition Key | Unique device identifier |
| type | String | - | Device type (AC, FAN, SPEAKER) |
| name | String | - | User-defined device name |
| brand | String | - | Device brand (Haier, LG, Sony) |
| on | Boolean | - | Power state |
| online | Boolean | - | Network connection state |
| level | Number | - | Operational level |
| powerUsage | Number | - | Current power consumption (W) |

#### 4.1.2 SensorData Table

| Attribute | Type | Key | Description |
|-----------|------|-----|-------------|
| id | String | Partition Key | Unique reading identifier |
| deviceId | String | GSI Partition Key | Associated device ID |
| date | String | - | Reading date (YYYY-MM-DD) |
| time | String | - | Reading time (HH:mm:ss) |
| data | String | - | Sensor reading message |

### 4.2 Data Relationships

```
Device (1) ←→ (N) SensorData
  ↑
  └─ deviceId foreign key relationship
```

### 4.3 Query Patterns

1. **Find Device by ID**: `table.getItem(partitionKey = deviceId)`
2. **Get All Devices**: `table.scan()`
3. **Find Sensor Data by Device**: `GSI query on deviceId`
4. **Find Sensor Data by Date Range**: `scan with filter expression`

---

## 5. API Design

### 5.1 Internal Service APIs

#### 5.1.1 Device Management API

```java
// Device Registration
POST /devices
{
  "type": "AC",
  "brand": "Samsung", 
  "name": "Living Room AC"
}
Response: { "id": "20240915123000", "status": "registered" }

// Device Control
PUT /devices/{id}/toggle
Response: { "id": "deviceId", "on": true, "powerUsage": 850 }

// Device Simulation
PUT /devices/{id}/simulate
{
  "level": 25
}
Response: { "id": "deviceId", "level": 25, "powerUsage": 450 }
```

#### 5.1.2 Network Management API

```java
// Connect Device
PUT /devices/{id}/connect
Response: { "id": "deviceId", "online": true }

// Network Topology
GET /network/topology
Response: {
  "server": "localhost:5555",
  "connectedDevices": [
    {"id": "AC001", "name": "Living Room AC", "online": true},
    {"id": "FAN001", "name": "Bedroom Fan", "online": true}
  ]
}
```

#### 5.1.3 Sensor Data API

```java
// Get Device Readings
GET /sensors/device/{id}?limit=10
Response: [
  {
    "id": "20240915123045001",
    "deviceId": "AC001",
    "date": "2024-09-15",
    "time": "12:30:45",
    "data": "Living Room AC: 25°C, Power: 450W"
  }
]

// Get Readings by Date Range
GET /sensors/range?start=2024-09-14&end=2024-09-15
Response: [ /* array of sensor readings */ ]
```

### 5.2 Network Protocol Design

#### 5.2.1 TCP Communication Messages

```
// Device Connection
Client → Server: "CONNECT Living Room AC"
Server → Client: "OK Connected"

// Device Disconnection  
Client → Server: "DISCONNECT Living Room AC"
Server → Client: "OK Disconnected"

// Sensor Data Transmission
Client → Server: "SENSOR Living Room AC: 25°C, Power: 450W"
Server → Client: "OK Received"
```

---

## 6. User Interface Design

### 6.1 Console Interface Layout

#### 6.1.1 Welcome Screen

```
   _____                      _     _    _                      
  / ____|                    | |   | |  | |                     
 | (___  _ __ ___   __ _ _ __| |_  | |__| | ___  _ __ ___   ___ 
  \___ \| '_ ` _ \ / _` | '__| __| |  __  |/ _ \| '_ ` _ \ / _ \
  ____) | | | | | | (_| | |  | |_  | |  | | (_) | | | | | |  __/
 |_____/|_| |_| |_|\__,_|_|   \__| |_|  |_|\___/|_| |_| |_|\___| 
                                                                
               IoT Dashboard & Control System                   

Welcome to Your Smart Home Control Center
Connecting to your smart devices...
Loading device registry...
```

#### 6.1.2 Main Menu Structure

```
╔════════════════════════════════════════╗
║        🏠 SMART HOME DASHBOARD 🏠       ║
╠════════════════════════════════════════╣
║  [1] Device Management                 ║
║  [2] Network Management                ║
║  [3] Sensor Readings                   ║
║  [4] Live Dashboard                    ║
║  [0] Exit                              ║
╚════════════════════════════════════════╝
```

#### 6.1.3 Device Management Submenu

```
╔════════════════════════════════════════╗
║         DEVICE MANAGEMENT              ║
╠════════════════════════════════════════╣
║  CURRENT DEVICES                       ║
║  • AC001: Living Room AC (ON, Online)  ║
║  • FAN001: Bedroom Fan (OFF, Offline) ║
║                                        ║
║  [1] Register Device                   ║
║  [2] Deregister Device                 ║
║  [3] Toggle On/Off                     ║
║  [4] Simulate Operation                ║
║  [0] Back                              ║
╚════════════════════════════════════════╝
```

### 6.2 Color Coding System

| Color | ANSI Code | Usage |
|-------|-----------|-------|
| GREEN | `\u001B[32m` | Success messages, ON states |
| RED | `\u001B[31m` | Error messages, OFF states |
| YELLOW | `\u001B[33m` | Warning messages, processing |
| CYAN | `\u001B[36m` | Information, menu options |
| PURPLE | `\u001B[35m` | Data display, device info |
| BLUE | `\u001B[34m` | User input prompts |

### 6.3 Real-time Dashboard

```
╔══════════════════════════════════════════════╗
║              📊 LIVE DASHBOARD               ║
╠══════════════════════════════════════════════╣
║                                              ║
║  🌡️  Living Room AC: 24°C, Power: 530W      ║
║  💨 Bedroom Fan: Speed 3, Power: 35W        ║
║  🔊 Kitchen Speaker: Volume 45%, Power: 27W ║
║                                              ║
║  Press Enter to stop monitoring...          ║
╚══════════════════════════════════════════════╝
```

---

## 7. Data Flow Design

### 7.1 Device Registration Flow

```
User Input → ConsoleMenu → SmartHomeController → SmartHomeService
    ↓
InputValidator.validateName()
    ↓
ApplianceFactory.createDevice()
    ↓
IdGenerator.generateID()
    ↓
ApplianceDB.save() → DynamoDB
    ↓
Success Response → User
```

### 7.2 Device Control Flow

```
User Command → ConsoleMenu → SmartHomeController → SmartHomeService
    ↓
Find Device in Memory Cache
    ↓
Create Command (Toggle/Simulate)
    ↓
Remote.setCommand() → Remote.pressButton()
    ↓
Command.execute() → Appliance.toggleOnOff()/simulate()
    ↓
Appliance.notifyObservers() → DeviceObserver.update()
    ↓
Power Calculation → State Update
```

### 7.3 Sensor Data Streaming Flow

```
StreamThread (Every 3 seconds)
    ↓
For Each Active Device
    ↓
SensorDataGenerator.generateMessage()
    ↓
Client.sendSensorReading() → Server
    ↓
Every 33rd Reading
    ↓
SensorData.Builder.build()
    ↓
SensorDataDB.save() → DynamoDB
    ↓
Continue Loop
```

### 7.4 Network Topology Flow

```
User Request → Controller → Service
    ↓
Get All Devices from Memory
    ↓
Filter Online Devices
    ↓
Graph.addNode() for each device
    ↓
Graph.addEdge() Server → Device
    ↓
Graph.showTopology() → String representation
    ↓
Display to User
```

---

## 8. Security Design

### 8.1 Input Validation

```java
public class InputValidator {
    // Device name validation (letters and spaces only)
    public static void validateName(String name) throws UserException {
        if (!name.matches("^[a-zA-Z\\s]+$")) {
            throw new UserException("Name must contain only letters and spaces");
        }
    }
    
    // Parameter range validations
    public static void checkTemperatureRange(String input) throws UserException {
        int temp = Integer.parseInt(input.trim());
        if (temp < 17 || temp > 30) {
            throw new UserException("Temperature must be between 17-30°C");
        }
    }
    
    public static void checkVolumeRange(String input) throws UserException {
        int volume = Integer.parseInt(input.trim());
        if (volume < 1 || volume > 100) {
            throw new UserException("Volume must be between 1-100%");
        }
    }
    
    public static void checkSpeedRange(String input) throws UserException {
        int speed = Integer.parseInt(input.trim());
        if (speed < 1 || speed > 5) {
            throw new UserException("Speed must be between 1-5 levels");
        }
    }
}
```

### 8.2 Exception Handling Hierarchy

```java
// Base application exception
public abstract class ApplicationException extends Exception {
    protected ApplicationException(String message) { super(message); }
    protected ApplicationException(String message, Throwable cause) { super(message, cause); }
}

// Specific exception types
public class DeviceException extends ApplicationException { /* ... */ }
public class DatabaseException extends ApplicationException { /* ... */ }
public class NetworkException extends ApplicationException { /* ... */ }
public class UserException extends ApplicationException { /* ... */ }
```

### 8.3 AWS Security

```java
// DynamoDB connection with proper error handling
public class DynamoDBConnection {
    private static DynamoDbClient instance;
    
    public static synchronized DynamoDbClient getInstance() {
        if (instance == null) {
            try {
                instance = DynamoDbClient.builder()
                    .region(Region.US_EAST_1)
                    .build();
            } catch (Exception e) {
                System.err.println("Cannot connect to DynamoDB: " + e.getMessage());
                return null; // Graceful degradation
            }
        }
        return instance;
    }
}
```

### 8.4 Thread Safety

- `ConcurrentHashMap` for device cache
- `CopyOnWriteArrayList` for observer lists
- `AtomicBoolean` for streaming control
- Synchronized methods for device state changes

---

## 9. Performance Design

### 9.1 Caching Strategy

- **In-Memory Device Cache**: All devices loaded at startup
- **Lazy Loading**: Database connections established on demand
- **Batch Operations**: Sensor data saved every 33rd reading

### 9.2 Concurrent Operations

- **Background Sensor Streaming**: Separate daemon thread
- **Network Server**: Non-blocking TCP server
- **Thread-Safe Collections**: Used throughout the system

### 9.3 Resource Management

```java
// Proper resource cleanup in AppConfig.shutdown()
public void shutdown() {
    // Stop streaming first
    smartHomeService.stopSensorStream();
    
    // Close network connections
    if (server != null) server.stop();
    if (client != null) client.close();
    
    // Save final state to database
    smartHomeService.shutdownSystem();
}
```

---

## 10. Testing Design

### 10.1 Test Architecture

- **Unit Tests**: 243 comprehensive tests using JUnit 5
- **Integration Tests**: Database and network integration
- **Test Coverage**: Minimum 85% code coverage target
- **Mock Objects**: Custom test observers and DAOs

### 10.2 Test Categories

| Category | Count | Coverage |
|----------|-------|----------|
| Model Tests | ~70 | Constructor, getters/setters, business logic |
| Service Tests | ~50 | Business logic, error handling |
| Utility Tests | ~65 | Input validation, date/time utilities |
| Factory Tests | ~30 | Factory pattern implementations |
| Command Tests | ~35 | Command pattern execution |
| Integration Tests | ~15 | End-to-end workflows |

---

*This design document provides comprehensive technical specifications for implementing and maintaining the IoT Smart Home Dashboard system. All design decisions are based on established software engineering principles and patterns.*