# Software Requirements - IoT Smart Home Dashboard

**Technologies**: Java 21, Maven, AWS DynamoDB, Docker

## What the Application Does

This is a console-based application that manages smart home devices. Users can:
- Add and remove smart devices (AC, Fan, Speaker)
- Control devices (turn on/off, adjust settings)
- Monitor real-time sensor data
- View device network connections
- Store data in AWS DynamoDB database

## System Requirements

### Hardware Requirements
- **Minimum**: 2-core CPU, 512MB RAM, 100MB storage
- **Recommended**: 4-core CPU, 1GB+ RAM, 500MB storage
- **Network**: Internet connection for cloud database

### Software Requirements
- Java 21 or higher
- Maven 3.6+
- AWS DynamoDB (cloud or local)
- Docker (optional)
- Compatible with Windows, Linux, macOS

### User Types
- **Smart Home Owner**: Main user who manages devices
- **System Administrator**: Handles maintenance and troubleshooting
- **Developer**: Extends and maintains the system

## Main Features

### 1. Device Management
- Register new devices (AC, Fan, Speaker)
- Support for 3 brands: Haier, LG, Sony
- Remove devices from system
- Generate unique device IDs automatically
- Validate device names (letters and spaces only)
- Save device information to database

### 2. Device Control
- Turn devices on/off
- Simulate device operations:
  - AC: Set temperature (17-30°C)
  - Fan: Set speed (1-5 levels)
  - Speaker: Set volume (1-100%)
- Calculate power consumption for each device
- Prevent control of offline devices

### 3. Network Management
- Connect/disconnect devices from network
- Show network topology (which devices are connected)
- Use TCP/IP simulation on port 5555
- Track device online/offline status

### 4. Sensor Data
- Stream real-time sensor data every 3 seconds
- Save data to database periodically
- Show historical data by device or date range
- Display live dashboard with current readings

### 5. User Interface
- Console-based menu system
- Color-coded messages (green=success, red=error, etc.)
- ASCII art welcome screen
- Input validation with helpful error messages

## Performance Requirements

### Response Times
- Device operations: Less than 100ms
- Sensor data processing: Less than 10ms
- Database operations: Less than 500ms

### Resource Usage
- Memory: Maximum 256MB
- CPU: Less than 30% during normal use
- Storage: Less than 100MB

### Data Throughput
- Sensor data: 1 reading per device every 3 seconds
- Database writes: Up to 100 per minute

## Design Constraints

### Technology Requirements
- Must use Java 21
- Must use Maven for builds
- Must use AWS DynamoDB for database
- Must implement these design patterns: Builder, Factory, Command, Observer, Singleton

### Interface Requirements
- Console-based only (no GUI)
- Menu-driven navigation
- Color-coded output
- Input validation for all user entries

### Data Requirements
- Two database tables: Device and SensorData
- Secure handling of AWS credentials
- Data validation before database operations
- Support both local and cloud DynamoDB

## Testing Requirements

- 240+ unit tests using JUnit 5
- 85% minimum code coverage
- Integration tests for database and network
- No external mocking frameworks

---

*This is a simplified requirements document for interview explanation purposes*
