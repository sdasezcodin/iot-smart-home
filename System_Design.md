# IoT Smart Home Dashboard - System Design

**Technologies**: Java 21, Maven, AWS DynamoDB, Docker

## Quick Overview

This is a console-based application that manages smart home devices like air conditioners, fans, and speakers. Users can control devices, monitor sensor data, and view network connections through a simple menu interface.

## System Architecture

The application is built using a layered architecture (MVC pattern):

```
User Interface (ConsoleMenu)
      ↓
Controller (SmartHomeController)
      ↓
Service Layer (SmartHomeService)
      ↓
Model Layer (Appliance, SensorData)
      ↓
Database Layer (DynamoDB)
```

### Main Components

- **ConsoleMenu**: Handles user input and displays menus
- **SmartHomeController**: Processes user requests
- **SmartHomeService**: Contains business logic
- **Appliance**: Represents smart devices (AC, Fan, Speaker)
- **ApplianceDB**: Manages database operations
- **Network**: Simulates device connections

## Design Patterns Used

### 1. Builder Pattern
**Purpose**: Creates complex objects step by step
**Example**: Building appliance objects with multiple properties
```java
Appliance ac = new Appliance.Builder()
    .id("AC001")
    .type("AC")
    .name("Living Room AC")
    .build();
```

### 2. Factory Pattern
**Purpose**: Creates objects without specifying exact classes
**Example**: Different factories for Haier, LG, Sony brands
```java
ApplianceFactory factory = new HaierFactory();
Appliance ac = factory.acBuilder().name("Bedroom AC").build();
```

### 3. Command Pattern
**Purpose**: Encapsulates requests as objects
**Example**: Toggle and Simulate commands for device control
```java
Command toggleCommand = new Toggle(appliance);
Remote remote = new Remote();
remote.setCommand(toggleCommand);
remote.pressButton(); // Executes the command
```

### 4. Observer Pattern
**Purpose**: Notifies multiple objects about state changes
**Example**: Dashboard gets notified when device state changes
```java
appliance.attach(dashboard);
appliance.toggleOnOff(); // Dashboard gets notified automatically
```

### 5. Singleton Pattern
**Purpose**: Ensures only one instance of a class exists
**Example**: Application configuration
```java
AppConfig config = AppConfig.init(); // Always returns same instance
```

## Key Features

### Device Management
- Support for 3 device types: AC, Fan, Speaker
- Support for 3 brands: Haier, LG, Sony
- Register/deregister devices
- Toggle power on/off
- Simulate device operations (temperature, speed, volume)

### Sensor Data
- Real-time sensor data streaming every 3 seconds
- Historical data storage in DynamoDB
- Query data by device or date range
- Live dashboard display

### Network Simulation
- TCP client-server architecture
- Connect/disconnect devices
- Network topology visualization
- Device online/offline status

### Database
- AWS DynamoDB for data persistence
- Two tables: Device and SensorData
- Local DynamoDB for testing
- DAO pattern for data access

## User Interface

The application uses a console-based menu system:

### Main Menu
- Device Management (register, control devices)
- Network Management (connect/disconnect)
- Sensor Readings (view historical data)
- Live Dashboard (real-time monitoring)

### Color Coding
- Green: Success messages, device ON
- Red: Error messages, device OFF
- Yellow: Warning messages
- Cyan: Information and menu options
- Purple: Data display

## Testing

- **Unit Tests**: 240+ comprehensive JUnit 5 tests
- **Test Coverage**: 85%+ code coverage
- **Categories**: Model, Service, Utility, Factory, Command, Integration tests
- **No External Mocks**: Uses custom test implementations

## Power Consumption Calculation

- **AC**: Base 50W + variable based on temperature
- **Fan**: 15W to 55W based on speed level (1-5)
- **Speaker**: Base 5W + volume-based consumption

---

*This is a simplified design document for interview explanation purposes*
