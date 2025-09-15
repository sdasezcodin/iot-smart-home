# Software Requirements Specification (SRS)
# IoT Smart Home Dashboard

---

**Document Version**: 1.0  
**Date**: September 15, 2025  
**Project**: IoT Smart Home Dashboard  
**Technology**: Java 21, Maven, AWS DynamoDB, Docker  

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Overall Description](#2-overall-description)
3. [System Features](#3-system-features)
4. [External Interface Requirements](#4-external-interface-requirements)
5. [System Features](#5-system-features-detailed)
6. [Functional Requirements](#6-functional-requirements)
7. [Non-Functional Requirements](#7-non-functional-requirements)
8. [Design Constraints](#8-design-constraints)
9. [System Evolution](#9-system-evolution)

---

## 1. Introduction

### 1.1 Purpose
This Software Requirements Specification (SRS) document describes the functional and non-functional requirements for the IoT Smart Home Dashboard system. The system provides a comprehensive solution for managing, monitoring, and controlling smart home appliances through a console-based interface with real-time data streaming and persistent storage capabilities.

### 1.2 Document Scope
This document covers all requirements for the IoT Smart Home Dashboard system including:
- Device management and control
- Real-time sensor data monitoring
- Network topology management
- Data persistence and retrieval
- User interface specifications
- Performance and security requirements

### 1.3 Definitions, Acronyms, and Abbreviations

| Term | Definition |
|------|------------|
| IoT | Internet of Things |
| SRS | Software Requirements Specification |
| API | Application Programming Interface |
| AWS | Amazon Web Services |
| DynamoDB | NoSQL database service by AWS |
| TCP/IP | Transmission Control Protocol/Internet Protocol |
| DAO | Data Access Object |
| MVC | Model-View-Controller |
| CLI | Command Line Interface |
| JUnit | Java Unit Testing Framework |
| Maven | Build automation tool for Java |

### 1.4 References
- AWS DynamoDB Documentation
- Java 21 Documentation
- Maven 3.9+ Documentation
- JUnit 5 Testing Framework
- Docker Documentation

### 1.5 Document Overview
This SRS follows IEEE standard format and describes the IoT Smart Home Dashboard system from functional, performance, and design perspectives. It serves as the primary reference for development, testing, and maintenance activities.

---

## 2. Overall Description

### 2.1 Product Perspective
The IoT Smart Home Dashboard is a standalone Java application that simulates and manages IoT smart home devices. The system operates as:

- **Console Application**: Primary user interface through command-line interaction
- **Client-Server Architecture**: Internal TCP/IP communication for device networking
- **Cloud-Integrated**: AWS DynamoDB for data persistence
- **Containerized**: Docker support for deployment
- **Real-time System**: Live sensor data streaming and monitoring

### 2.2 Product Functions
The system provides the following major functions:

1. **Device Management**
   - Register new smart appliances (AC, Fan, Speaker)
   - Deregister existing devices
   - Manage device states (on/off, online/offline)

2. **Device Control**
   - Toggle device power states
   - Simulate device operations (temperature, speed, volume)
   - Execute commands using Command pattern

3. **Network Management**
   - Connect/disconnect devices to/from network
   - Display network topology
   - Monitor connection status

4. **Sensor Data Management**
   - Real-time sensor data streaming
   - Historical data retrieval
   - Data persistence to cloud database
   - Analytics and reporting

5. **User Interface**
   - Console-based menu system
   - Real-time dashboard display
   - Color-coded output for better readability

### 2.3 User Classes and Characteristics

#### 2.3.1 Smart Home Owner (Primary User)
- **Technical Level**: Basic to Intermediate
- **Responsibilities**: Manage and monitor home devices
- **Usage Frequency**: Daily
- **Key Tasks**: Device control, monitoring, configuration

#### 2.3.2 System Administrator (Secondary User)
- **Technical Level**: Advanced
- **Responsibilities**: System maintenance, troubleshooting
- **Usage Frequency**: As needed
- **Key Tasks**: Database management, system configuration

#### 2.3.3 Developer (Tertiary User)
- **Technical Level**: Expert
- **Responsibilities**: System extension, maintenance
- **Usage Frequency**: During development cycles
- **Key Tasks**: Add new features, fix bugs, testing

### 2.4 Operating Environment

#### 2.4.1 Hardware Requirements
- **Minimum CPU**: 2 cores, 1.8 GHz
- **Recommended CPU**: 4+ cores, 2.4 GHz
- **Minimum RAM**: 512 MB
- **Recommended RAM**: 1 GB+
- **Storage**: 100 MB (minimum), 500 MB (recommended)
- **Network**: 1 Mbps (minimum), 10 Mbps (recommended)

#### 2.4.2 Software Requirements
- **Java Runtime**: Java 21 or higher
- **Build Tool**: Maven 3.6+
- **Database**: AWS DynamoDB (cloud) or DynamoDB Local
- **Container**: Docker (optional)
- **Operating System**: Windows, Linux, macOS

### 2.5 Design and Implementation Constraints

#### 2.5.1 Technology Constraints
- Must use Java 21 as primary programming language
- Must use Maven for build management
- Must use AWS DynamoDB for data persistence
- Must implement design patterns (Builder, Factory, Command, Observer, Singleton)

#### 2.5.2 Regulatory Constraints
- Must comply with data privacy regulations
- Must handle user data securely
- Must provide proper error handling and logging

#### 2.5.3 Hardware Limitations
- Console-based interface only (no GUI)
- Single-user operation per instance
- Local network communication simulation

### 2.6 User Documentation
The system includes:
- Comprehensive README.md with setup instructions
- In-code documentation (JavaDoc)
- Test documentation and summaries
- Architecture documentation
- API reference documentation

### 2.7 Assumptions and Dependencies

#### 2.7.1 Assumptions
- Users have basic command-line knowledge
- Network connectivity is available for cloud features
- Java 21 runtime is properly installed
- AWS credentials are configured for cloud features

#### 2.7.2 Dependencies
- AWS SDK for Java (DynamoDB support)
- Jackson library for JSON processing
- SLF4J for logging
- JUnit 5 for testing
- Maven for build management

---

## 3. System Features

### 3.1 Device Registration and Management

#### 3.1.1 Description
The system shall provide comprehensive device management capabilities allowing users to register, configure, and manage smart home appliances.

#### 3.1.2 Priority
High

#### 3.1.3 Functional Requirements

**FR-1.1**: The system shall support registration of three device types:
- Air Conditioner (AC) with temperature control (17-30°C)
- Fan with speed control (1-5 levels)  
- Speaker with volume control (1-100%)

**FR-1.2**: The system shall support three device brands:
- Haier
- LG  
- Sony

**FR-1.3**: The system shall generate unique IDs for each device using timestamp-based generation.

**FR-1.4**: The system shall validate user inputs for device names (letters and spaces only).

**FR-1.5**: The system shall persist device information to DynamoDB database.

**FR-1.6**: The system shall allow deregistration of devices with proper cleanup.

### 3.2 Device Control and Operation

#### 3.2.1 Description
The system shall provide real-time control of registered devices through command-pattern implementation.

#### 3.2.2 Priority
High

#### 3.2.3 Functional Requirements

**FR-2.1**: The system shall support power toggle (ON/OFF) for all devices.

**FR-2.2**: The system shall support device simulation with parameter validation:
- AC: Temperature range 17-30°C
- Fan: Speed levels 1-5
- Speaker: Volume 1-100%

**FR-2.3**: The system shall calculate and display power consumption:
- AC: 50W base + (30-temp)*80W variable load
- Fan: 15W/25W/35W/45W/55W for levels 1-5
- Speaker: 5W base + volume*0.5W

**FR-2.4**: The system shall notify observers of state changes using Observer pattern.

**FR-2.5**: The system shall prevent operations on offline or powered-off devices.

### 3.3 Network Management

#### 3.3.1 Description  
The system shall simulate network connectivity and provide topology visualization for smart home devices.

#### 3.3.2 Priority
Medium

#### 3.3.3 Functional Requirements

**FR-3.1**: The system shall simulate TCP/IP client-server communication on port 5555.

**FR-3.2**: The system shall support device connection/disconnection to/from network.

**FR-3.3**: The system shall display network topology showing server and connected devices.

**FR-3.4**: The system shall prevent network operations on powered-off devices.

**FR-3.5**: The system shall maintain device online/offline status persistently.

### 3.4 Sensor Data Management

#### 3.4.1 Description
The system shall provide real-time sensor data streaming, collection, and historical data management.

#### 3.4.2 Priority
High

#### 3.4.3 Functional Requirements

**FR-4.1**: The system shall stream sensor data every 3 seconds from active devices.

**FR-4.2**: The system shall save sensor readings to database every 33rd reading.

**FR-4.3**: The system shall generate realistic sensor data messages including:
- Device name and type
- Current operational level
- Power consumption
- Timestamp

**FR-4.4**: The system shall support historical data retrieval by:
- Device ID with limit (1-10 readings)
- Date range (YYYY-MM-DD format)

**FR-4.5**: The system shall provide real-time dashboard display with streaming data.

### 3.5 User Interface

#### 3.5.1 Description
The system shall provide an intuitive console-based user interface with menu navigation and real-time feedback.

#### 3.5.2 Priority
High  

#### 3.5.3 Functional Requirements

**FR-5.1**: The system shall display ASCII art welcome screen with loading animation.

**FR-5.2**: The system shall provide hierarchical menu system:
- Main Menu
- Device Management submenu
- Network Management submenu
- Sensor Data submenu
- Live Dashboard

**FR-5.3**: The system shall use color coding for different message types:
- Green: Success messages
- Red: Error messages  
- Yellow: Warning messages
- Cyan: Information messages
- Purple: Data display

**FR-5.4**: The system shall provide input validation with user-friendly error messages.

**FR-5.5**: The system shall support graceful exit with proper system shutdown.

---

## 4. External Interface Requirements

### 4.1 User Interfaces

#### 4.1.1 Console Interface
- **Type**: Text-based command line interface
- **Input Method**: Keyboard input via Scanner
- **Output Method**: System.out with ANSI color codes
- **Navigation**: Menu-driven with numeric selections
- **Response Time**: Immediate feedback for user actions

#### 4.1.2 Menu Structure
```
Main Menu
├── [1] Device Management
│   ├── [1] Register Device
│   ├── [2] Deregister Device
│   ├── [3] Toggle On/Off
│   ├── [4] Simulate Operation
│   └── [0] Back
├── [2] Network Management
│   ├── [1] Connect Device
│   ├── [2] Disconnect Device
│   └── [0] Back
├── [3] Sensor Readings
│   ├── [1] By Device
│   ├── [2] By Date Range
│   └── [0] Back
├── [4] Live Dashboard
└── [0] Exit
```

### 4.2 Hardware Interfaces
- **Input**: Standard keyboard
- **Output**: Console/terminal display
- **Storage**: Local file system for temporary data
- **Network**: TCP/IP localhost communication

### 4.3 Software Interfaces

#### 4.3.1 AWS DynamoDB
- **Interface Type**: RESTful API via AWS SDK
- **Tables**: 
  - `Device`: Device information and state
  - `SensorData`: Historical sensor readings
- **Operations**: CRUD operations, scan, query
- **Authentication**: AWS credentials (Access Key/Secret)

#### 4.3.2 Java Runtime
- **Version**: Java 21+
- **Features Used**: Records, Pattern Matching, Enhanced Switch
- **Threading**: Concurrent operations support
- **I/O**: Standard input/output streams

#### 4.3.3 Maven Dependencies
- AWS SDK for Java: 2.29.23
- Jackson Databind: 2.20.0
- SLF4J Simple: 2.0.16
- JUnit Jupiter: 5.10.0

### 4.4 Communication Interfaces

#### 4.4.1 Internal TCP Communication
- **Protocol**: TCP/IP
- **Port**: 5555 (configurable)
- **Host**: localhost
- **Message Format**: Plain text commands
- **Commands**: CONNECT, DISCONNECT, SENSOR

#### 4.4.2 Database Communication
- **Protocol**: HTTPS (AWS API)
- **Authentication**: IAM credentials
- **Data Format**: JSON
- **Operations**: Real-time read/write operations

---

## 5. System Features (Detailed)

### 5.1 Device Factory System

#### 5.1.1 Description
Implementation of Abstract Factory pattern for creating brand-specific appliances.

#### 5.1.2 Stimulus/Response Sequences

**Stimulus**: User selects device type and brand  
**Response**: System creates appropriate device instance using factory

#### 5.1.3 Associated Functional Requirements
- FR-1.1: Support multiple device types
- FR-1.2: Support multiple brands
- FR-1.3: Generate unique device IDs

### 5.2 Command Execution System

#### 5.2.1 Description
Implementation of Command pattern for device operations with undo/redo capability.

#### 5.2.2 Stimulus/Response Sequences

**Stimulus**: User requests device operation  
**Response**: System creates and executes appropriate command

#### 5.2.3 Associated Functional Requirements
- FR-2.1: Power toggle operations
- FR-2.2: Device simulation operations
- FR-2.3: Power consumption calculations

### 5.3 Observer Notification System

#### 5.3.1 Description
Implementation of Observer pattern for real-time device state monitoring.

#### 5.3.2 Stimulus/Response Sequences

**Stimulus**: Device state changes  
**Response**: All registered observers receive notifications

#### 5.3.3 Associated Functional Requirements
- FR-2.4: Observer notifications
- FR-4.1: Real-time sensor streaming
- FR-5.3: Color-coded messages

---

## 6. Functional Requirements

### 6.1 Device Management Requirements

| ID | Requirement | Priority | Complexity |
|----|-------------|----------|------------|
| FR-1.1 | Support AC, Fan, Speaker device types | High | Medium |
| FR-1.2 | Support Haier, LG, Sony brands | High | Low |
| FR-1.3 | Generate unique timestamp-based IDs | High | Medium |
| FR-1.4 | Validate device names (letters/spaces) | Medium | Low |
| FR-1.5 | Persist devices to DynamoDB | High | High |
| FR-1.6 | Support device deregistration | High | Medium |

### 6.2 Device Control Requirements

| ID | Requirement | Priority | Complexity |
|----|-------------|----------|------------|
| FR-2.1 | Toggle device power states | High | Low |
| FR-2.2 | Validate simulation parameters | High | Medium |
| FR-2.3 | Calculate device power consumption | High | Medium |
| FR-2.4 | Notify observers of state changes | Medium | Medium |
| FR-2.5 | Prevent invalid operations | High | Low |

### 6.3 Network Management Requirements

| ID | Requirement | Priority | Complexity |
|----|-------------|----------|------------|
| FR-3.1 | TCP/IP client-server simulation | Medium | High |
| FR-3.2 | Device connect/disconnect operations | Medium | Medium |
| FR-3.3 | Network topology visualization | Low | Medium |
| FR-3.4 | Validate network operations | Medium | Low |
| FR-3.5 | Persist network status | Medium | Medium |

### 6.4 Sensor Data Requirements

| ID | Requirement | Priority | Complexity |
|----|-------------|----------|------------|
| FR-4.1 | Stream sensor data every 3 seconds | High | High |
| FR-4.2 | Batch save every 33rd reading | Medium | Medium |
| FR-4.3 | Generate realistic sensor messages | High | Medium |
| FR-4.4 | Historical data retrieval | High | High |
| FR-4.5 | Real-time dashboard display | High | High |

### 6.5 User Interface Requirements

| ID | Requirement | Priority | Complexity |
|----|-------------|----------|------------|
| FR-5.1 | ASCII art welcome screen | Low | Low |
| FR-5.2 | Hierarchical menu system | High | Medium |
| FR-5.3 | Color-coded message display | Medium | Low |
| FR-5.4 | Input validation and error handling | High | Medium |
| FR-5.5 | Graceful system shutdown | High | Medium |

---

## 7. Non-Functional Requirements

### 7.1 Performance Requirements

#### 7.1.1 Response Time
- **Device Registration**: < 100ms
- **Command Execution**: < 50ms
- **Sensor Data Processing**: < 10ms
- **Network Topology Generation**: < 200ms
- **Database Operations**: < 500ms

#### 7.1.2 Throughput
- **Sensor Data Stream**: 1 reading per device every 3 seconds
- **Concurrent Operations**: Support up to 10 simultaneous device operations
- **Database Writes**: Support up to 100 writes per minute

#### 7.1.3 Resource Usage
- **Memory**: Maximum 256 MB heap size
- **CPU**: < 30% utilization during normal operation
- **Disk Space**: < 100 MB for application and logs
- **Network**: < 1 KB/s for sensor data transmission

### 7.2 Safety Requirements

#### 7.2.1 Data Integrity
- **Transactional Operations**: All database operations must be atomic
- **Data Validation**: All inputs must be validated before processing
- **Error Recovery**: System must recover gracefully from failures
- **State Consistency**: Device states must remain consistent across operations

#### 7.2.2 Failure Handling
- **Database Connection Loss**: Continue operation with in-memory data
- **Network Failures**: Queue operations for retry when connection restored
- **Invalid Input**: Reject and provide clear error messages
- **System Crashes**: Persist critical data before shutdown

### 7.3 Security Requirements

#### 7.3.1 Authentication
- **AWS Access**: Valid AWS credentials required for database operations
- **Input Validation**: All user inputs must be sanitized
- **SQL Injection Prevention**: Use parameterized queries/prepared statements
- **Error Message Security**: No sensitive information in error messages

#### 7.3.2 Data Protection
- **Encryption**: Data encrypted in transit to AWS DynamoDB
- **Access Control**: Database access through IAM policies
- **Audit Trail**: Log all significant system operations
- **Data Privacy**: No personal data stored without explicit consent

### 7.4 Software Quality Attributes

#### 7.4.1 Reliability
- **Availability**: 99% uptime during operation hours
- **Fault Tolerance**: Continue operation with degraded functionality
- **Error Handling**: Comprehensive exception handling throughout
- **Testing Coverage**: Minimum 85% code coverage with unit tests

#### 7.4.2 Usability
- **Learning Curve**: New users productive within 15 minutes
- **Menu Navigation**: Intuitive menu structure with clear options
- **Error Messages**: Clear, actionable error messages
- **Help Documentation**: Comprehensive README and documentation

#### 7.4.3 Maintainability
- **Code Organization**: Clear separation of concerns (MVC pattern)
- **Documentation**: JavaDoc for all public methods and classes
- **Design Patterns**: Consistent use of established design patterns
- **Testing**: Comprehensive unit and integration tests

#### 7.4.4 Portability
- **Platform Independence**: Run on Windows, Linux, macOS
- **Java Version**: Compatible with Java 21+
- **Container Support**: Fully containerized with Docker
- **Dependency Management**: All dependencies managed through Maven

### 7.5 Design Constraints

#### 7.5.1 Programming Language
- Must be implemented in Java 21
- Must use modern Java features (records, pattern matching, enhanced switch)
- Must follow Java naming conventions and best practices

#### 7.5.2 Architecture Constraints
- Must implement MVC architectural pattern
- Must use dependency injection for component relationships
- Must implement specified design patterns (Builder, Factory, Command, Observer, Singleton)
- Must maintain clear separation between layers

#### 7.5.3 Database Constraints
- Must use AWS DynamoDB for data persistence
- Must implement proper DAO pattern for data access
- Must handle database connection failures gracefully
- Must support both local DynamoDB and cloud instances

---

## 8. System Evolution

### 8.1 Future Enhancements

#### 8.1.1 Phase 2 - Planned Features
- REST API development for external integrations
- Web-based dashboard as alternative to console interface
- Mobile app support for remote device management
- Cloud deployment automation with CI/CD pipeline
- Advanced analytics and reporting capabilities

#### 8.1.2 Phase 3 - Future Vision
- Machine learning integration for predictive analytics
- Voice control support (Alexa, Google Assistant)
- IoT device protocol support (MQTT, CoAP, Zigbee)
- Multi-tenant architecture for multiple homes
- Real-time alerting and notification system

### 8.2 Scalability Considerations
- Modular architecture allows for easy feature additions
- Factory pattern enables new device types without code changes
- Command pattern allows new operations without modifying core logic
- Observer pattern supports unlimited observers for monitoring
- Database design supports horizontal scaling

### 8.3 Technology Evolution
- Java version upgrades supported through Maven configuration
- AWS SDK updates handled through dependency management
- Container orchestration ready (Kubernetes, Docker Swarm)
- Cloud-native deployment patterns supported
- Microservices architecture migration possible

---

## 9. Verification and Validation

### 9.1 Testing Strategy

#### 9.1.1 Unit Testing
- **Framework**: JUnit 5
- **Coverage Target**: Minimum 85% code coverage
- **Test Count**: 243 comprehensive unit tests
- **Categories**: Constructor, getter/setter, business logic, edge cases

#### 9.1.2 Integration Testing
- **Database Integration**: Test with real and mock DynamoDB instances
- **Network Integration**: Test client-server communication
- **End-to-End Testing**: Complete user workflow validation
- **Performance Testing**: Load and stress testing for sensor streaming

#### 9.1.3 Acceptance Testing
- **User Acceptance**: Validate all functional requirements
- **Performance Acceptance**: Verify non-functional requirements
- **Security Testing**: Input validation and injection prevention
- **Compatibility Testing**: Multi-platform Java compatibility

### 9.2 Validation Criteria
- All functional requirements must pass acceptance tests
- Performance requirements must be met under normal load
- Security requirements must be verified through penetration testing
- Usability requirements must be validated through user testing
- All 243 unit tests must pass with green status

---

## Appendix A: Glossary

**Appliance**: A smart home device that can be controlled and monitored through the system

**Builder Pattern**: A design pattern for constructing complex objects step by step

**Command Pattern**: A design pattern that encapsulates a request as an object

**Factory Pattern**: A design pattern for creating objects without specifying exact classes

**Observer Pattern**: A design pattern where objects are notified of state changes

**Sensor Data**: Time-stamped readings from smart home devices including operational parameters

**Singleton Pattern**: A design pattern ensuring only one instance of a class exists

---

## Appendix B: Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-09-15 | System Analyst | Initial SRS document creation |

---

*This document serves as the complete Software Requirements Specification for the IoT Smart Home Dashboard system. All requirements are traceable and verifiable through the comprehensive test suite.*