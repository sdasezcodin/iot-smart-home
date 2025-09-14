# IoT Smart Home Dashboard 🏠🌐

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![AWS SDK](https://img.shields.io/badge/AWS-DynamoDB-orange.svg)](https://aws.amazon.com/dynamodb/)
[![Tests](https://img.shields.io/badge/Tests-243%20Passing-brightgreen.svg)](#testing)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](#license)

A comprehensive **console-based IoT Smart Home Dashboard** built with Java 21, featuring real-time sensor monitoring, device control, network topology management, and robust data persistence using AWS DynamoDB.

## 🌟 Features

### Core Functionality
- 🏠 **Smart Appliance Management** - Register, control, and monitor AC units, fans, and speakers.
- 📊 **Real-time Dashboard** - Live sensor data streaming with background processing
- 🌐 **Network Topology** - Visual representation of connected devices and network structure
- 💾 **Data Persistence** - AWS DynamoDB integration for device state and sensor data
- 🎛️ **Device Control** - Toggle power states and simulate device operations
- 📡 **Network Simulation** - Client-server architecture with real TCP/IP communication

### Technical Highlights
- ⚡ **Modern Java 21** - Latest language features and performance improvements
- 🔄 **Design Patterns** - Implementation of Builder, Factory, Command, Observer, and Singleton patterns
- 🧵 **Thread Safety** - Concurrent operations with proper synchronization
- 🛡️ **Exception Handling** - Comprehensive error management with custom exception hierarchy
- ✅ **Extensive Testing** - 243 unit and integration tests with 100% success rate
- 🐳 **Docker Support** - Multi-stage builds for optimized container deployment

## 🏗️ Architecture

### System Components

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Console UI    │    │   Controller    │    │    Service      │
│  (View Layer)   │◄──►│     Layer       │◄──►│     Layer       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Network       │    │      Model      │    │   Data Access   │
│   Layer         │    │     Layer       │    │     Layer       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
        │                        │                        │
        ▼                        ▼                        ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  TCP Server     │    │   Appliances    │    │   DynamoDB      │
│  TCP Client     │    │  Sensor Data    │    │   Database      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Design Patterns Implemented

| Pattern | Implementation | Purpose |
|---------|----------------|---------|
| **Builder** | `Appliance.Builder`, `SensorData.Builder` | Complex object creation |
| **Factory** | `HaierFactory`, `LGFactory`, `SonyFactory` | Brand-specific appliance creation |
| **Command** | `Toggle`, `Simulate`, `Remote` | Device operation encapsulation |
| **Observer** | `DeviceObserver`, `Subject` | Real-time state notifications |
| **Singleton** | `AppConfig` | Single configuration instance |
| **MVC** | Controller-Service-View separation | Clean architecture |

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+** 
- **Docker** (optional, for containerized deployment)
- **AWS Account** (optional, for DynamoDB features)

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd "IoT Smarthome Dashboard"
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

3. **Run tests** (optional but recommended)
   ```bash
   mvn test
   ```

4. **Create executable JAR**
   ```bash
   mvn clean package
   ```

5. **Run the application**
   ```bash
   java -jar target/iot-smarthome-dashboard-1.0-SNAPSHOT.jar
   ```

### Docker Deployment

1. **Build Docker image**
   ```bash
   docker build -t smart-home-dashboard .
   ```

2. **Run container**
   ```bash
   docker run -it smart-home-dashboard
   ```

## 📖 Usage Guide

### Main Menu Navigation

When you start the application, you'll see the main menu:

```
╔════════════════════════════════════════╗
║        🏠 SMART HOME DASHBOARD 🏠       ║
╠════════════════════════════════════════╣
║  1. 📱 Device Management               ║
║  2. 🎛️  Device Control                 ║
║  3. 🌐 Network Management              ║
║  4. 📊 Sensor Data & Analytics         ║
║  5. 🔄 Live Dashboard                  ║
║  6. 🚪 Exit System                     ║
╚════════════════════════════════════════╝
```

### Device Management

#### Register a New Device
```
Select device type:
1. AC (Air Conditioner)
2. FAN 
3. SPEAKER

Select brand:
1. Haier
2. LG  
3. Sony

Enter device name: Living Room AC
```

#### Device Control Examples
- **Toggle Power**: Turn devices ON/OFF
- **Simulate Operations**:
  - AC: Set temperature (17-30°C)
  - Fan: Set speed (1-5)
  - Speaker: Set volume (1-100%)

### Network Features

#### View Topology
```
CURRENT NETWORK TOPOLOGY:

Server
├── Living Room AC (Online)
├── Bedroom Fan (Online)
└── Kitchen Speaker (Offline)
```

#### Sensor Data Dashboard
```
📊 LIVE SENSOR READINGS:

🌡️  Living Room AC: 24°C, Power: 530W
💨 Bedroom Fan: Speed 3, Power: 35W  
🔊 Kitchen Speaker: Volume 45%, Power: 27W
```

## 🧪 Testing

### Test Coverage

The project includes **243 comprehensive tests** covering:

- **Unit Tests**: Individual component testing
- **Integration Tests**: Multi-component interaction testing  
- **Edge Cases**: Boundary condition and error scenario testing
- **Thread Safety**: Concurrent operation testing
- **Performance Tests**: Load and stress testing

### Test Categories

| Test Class | Test Count | Coverage |
|------------|------------|----------|
| `ApplianceTest` | ~45 | Model layer, device operations |
| `ApplianceFactoryTest` | ~30 | Factory pattern implementation |
| `CommandPatternTest` | ~35 | Command execution and remote control |
| `ExceptionTest` | ~35 | Error handling and exception hierarchy |
| `InputValidatorTest` | ~40 | Input validation and sanitization |
| `DateTimeUtilTest` | ~20 | Utility functions and date handling |
| `IdGeneratorTest` | ~25 | ID generation and uniqueness |
| `SmartHomeIntegrationTest` | ~15 | End-to-end system testing |

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class  
mvn test -Dtest=ApplianceTest

# Run tests with verbose output
mvn test -Dtest=ApplianceTest -DforkCount=0

# Generate test report
mvn surefire-report:report
```

## 🔧 Configuration

### Application Configuration

The system uses `AppConfig` for centralized configuration management:

```java
// Network settings
private static final int SERVER_PORT = 5555;
private static final String SERVER_HOST = "localhost";

// Streaming settings  
private static final int STREAM_INTERVAL_MS = 3000;
private static final int SAVE_INTERVAL_READINGS = 33;
```

### AWS DynamoDB Setup

1. **Configure AWS credentials**:
   ```bash
   aws configure
   # or set environment variables:
   # AWS_ACCESS_KEY_ID=your-key
   # AWS_SECRET_ACCESS_KEY=your-secret
   # AWS_REGION=your-region
   ```

2. **Create DynamoDB tables**:
   - `Appliances` - Device information and state
   - `SensorData` - Historical sensor readings

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | 5555 | TCP server port |
| `STREAM_INTERVAL` | 3000 | Sensor data interval (ms) |
| `AWS_REGION` | us-east-1 | AWS DynamoDB region |

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/smarthome/
│   │   ├── SmartHomeApp.java              # Application entry point
│   │   ├── command/                       # Command pattern implementations
│   │   │   ├── Command.java
│   │   │   ├── Remote.java
│   │   │   ├── Simulate.java
│   │   │   └── Toggle.java
│   │   ├── config/                        # Configuration management
│   │   │   ├── AppConfig.java
│   │   │   └── DynamoDBConnection.java
│   │   ├── controller/                    # MVC Controller layer
│   │   │   └── SmartHomeController.java
│   │   ├── db/                           # Data Access Objects
│   │   │   ├── ApplianceDAO.java
│   │   │   ├── ApplianceDB.java
│   │   │   ├── SensorDataDAO.java
│   │   │   └── SensorDataDB.java
│   │   ├── exception/                    # Custom exceptions
│   │   │   ├── ApplicationException.java
│   │   │   ├── DatabaseException.java
│   │   │   ├── DeviceException.java
│   │   │   ├── NetworkException.java
│   │   │   └── UserException.java
│   │   ├── factory/                      # Factory pattern
│   │   │   ├── ApplianceFactory.java
│   │   │   ├── HaierFactory.java
│   │   │   ├── LGFactory.java
│   │   │   └── SonyFactory.java
│   │   ├── model/                        # Data models
│   │   │   ├── Appliance.java
│   │   │   ├── Device.java
│   │   │   └── SensorData.java
│   │   ├── network/                      # Network layer
│   │   │   ├── Client.java
│   │   │   └── Server.java
│   │   ├── observer/                     # Observer pattern
│   │   │   ├── Observer.java
│   │   │   └── Subject.java
│   │   ├── service/                      # Business logic layer
│   │   │   └── SmartHomeService.java
│   │   ├── util/                         # Utility classes
│   │   │   ├── ConsoleColorsUtil.java
│   │   │   ├── DateTimeUtil.java
│   │   │   ├── Graph.java
│   │   │   ├── IdGenerator.java
│   │   │   ├── InputValidator.java
│   │   │   └── SensorDataGenerator.java
│   │   └── view/                         # View layer
│   │       ├── ConsoleMenu.java
│   │       └── DeviceObserver.java
│   └── resources/
│       ├── devices.json                  # Sample device data
│       └── device_types.json            # Device type mappings
└── test/
    └── java/com/smarthome/              # Comprehensive test suite
        ├── command/CommandPatternTest.java
        ├── exception/ExceptionTest.java
        ├── factory/ApplianceFactoryTest.java
        ├── integration/SmartHomeIntegrationTest.java
        ├── model/ApplianceTest.java
        ├── model/SensorDataTest.java
        └── util/
            ├── DateTimeUtilTest.java
            ├── IdGeneratorTest.java
            └── InputValidatorTest.java
```

## 🛠️ Development

### Building from Source

```bash
# Compile only
mvn compile

# Compile and run tests
mvn test-compile

# Create JAR with dependencies
mvn clean package

# Clean build artifacts  
mvn clean
```

### Adding New Device Types

1. **Extend the factory pattern**:
   ```java
   public class CustomBrandFactory implements ApplianceFactory {
       @Override
       public Appliance.Builder customDeviceBuilder() {
           return new Appliance.Builder()
               .brand("CustomBrand")
               .type("CUSTOM_DEVICE");
       }
   }
   ```

2. **Update device type mappings** in `device_types.json`

3. **Add corresponding tests** following the existing pattern

### Adding New Commands

1. **Implement Command interface**:
   ```java
   public class CustomCommand implements Command {
       @Override
       public void execute() {
           // Implementation
       }
   }
   ```

2. **Add to controller and service layers**

3. **Include comprehensive tests**

## 🔍 Monitoring & Debugging

### Logging

The application uses SLF4J with simple logging:

```java
// Enable debug logging
System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");
```

### Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Port already in use | Change `SERVER_PORT` in `AppConfig` |
| AWS credentials not found | Configure using `aws configure` |
| Database connection failed | Check AWS region and credentials |
| Tests failing | Ensure Java 21 and proper Maven setup |

## 🤝 Contributing

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Write tests** for your changes
4. **Ensure all tests pass**: `mvn test`
5. **Commit your changes**: `git commit -m 'Add amazing feature'`
6. **Push to branch**: `git push origin feature/amazing-feature`
7. **Open a Pull Request**

### Code Style Guidelines

- Follow **Java naming conventions**
- Use **meaningful variable names**
- Add **comprehensive JavaDoc comments**
- Ensure **thread safety** for concurrent operations
- Write **unit tests** for all new functionality
- Follow **SOLID principles**

## 📊 Performance Specifications

### System Requirements

| Component | Minimum | Recommended |
|-----------|---------|-------------|
| **CPU** | 2 cores | 4+ cores |
| **RAM** | 512MB | 1GB+ |
| **Storage** | 100MB | 500MB+ |
| **Network** | 1Mbps | 10Mbps+ |

### Performance Metrics

- **Device Registration**: < 100ms
- **Command Execution**: < 50ms  
- **Sensor Data Processing**: < 10ms
- **Network Topology Build**: < 200ms
- **Database Operations**: < 500ms

## 🛡️ Security Considerations

- **Input Validation**: All user inputs are validated and sanitized
- **Exception Handling**: Sensitive information is not exposed in error messages
- **Resource Management**: Proper cleanup of network connections and threads
- **Database Security**: Uses AWS IAM for DynamoDB access control

## 📚 API Reference

### Core Classes

#### Appliance
```java
public class Appliance implements Device, Subject {
    // Device control methods
    public void toggleOnOff();
    public void simulate(int level);
    
    // Observer pattern methods
    public void attach(Observer o);
    public void notifyObservers(String message);
    
    // Power calculation
    public int calculatePower(int level);
}
```

#### SmartHomeService  
```java
public class SmartHomeService {
    // Device management
    public void registerDevice(String type, String brand, String name);
    public void deregisterDevice(String id);
    
    // Device control
    public void toggleDevice(String deviceId);
    public void simulateDevice(String deviceId, String level);
    
    // Network operations
    public void connectDevice(String deviceId);
    public String buildNetworkTopology();
    
    // Sensor operations
    public void startSensorStream();
    public List<SensorData> getReadingsByDevice(String deviceId, String limit);
}
```

## 🚀 Roadmap

### Phase 1 - Current ✅
- [x] Console-based interface
- [x] Device management system
- [x] Real-time sensor streaming
- [x] Network topology visualization
- [x] AWS DynamoDB integration

### Phase 2 - Planned 🚧
- [ ] REST API development
- [ ] Web-based dashboard  
- [ ] Mobile app support
- [ ] Cloud deployment automation
- [ ] Advanced analytics and reporting

### Phase 3 - Future 🔮
- [ ] Machine learning integration
- [ ] Voice control support
- [ ] IoT device protocol support (MQTT, CoAP)
- [ ] Multi-tenant architecture
- [ ] Real-time alerting system

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **AWS SDK** for DynamoDB integration
- **JUnit 5** for testing framework
- **Maven** for build management
- **Docker** for containerization
- **Java Community** for continuous innovation

## 📞 Support & Contact

- **Issues**: [GitHub Issues](https://github.com/your-username/iot-smarthome-dashboard/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-username/iot-smarthome-dashboard/discussions)
- **Documentation**: [Wiki](https://github.com/your-username/iot-smarthome-dashboard/wiki)

---

<div align="center">

**⭐ Star this project if you find it helpful!**

![Smart Home](https://img.shields.io/badge/Smart-Home-brightgreen.svg)
![IoT](https://img.shields.io/badge/IoT-Dashboard-blue.svg)
![Java](https://img.shields.io/badge/Made%20with-Java%2021-orange.svg)

*Built with ❤️ for the IoT community*

</div>
