package com.smarthome.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Custom Exception Tests")
class ExceptionTest {

    @Nested
    @DisplayName("ApplicationException Tests")
    class ApplicationExceptionTests {

        @Test
        @DisplayName("Should create ApplicationException with message")
        void shouldCreateApplicationExceptionWithMessage() {
            String message = "Application error occurred";
            ApplicationException exception = new ApplicationException(message);

            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should create ApplicationException with message and cause")
        void shouldCreateApplicationExceptionWithMessageAndCause() {
            String message = "Application error occurred";
            Exception cause = new RuntimeException("Root cause");
            ApplicationException exception = new ApplicationException(message, cause);

            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
            assertEquals(cause, exception.getCause());
        }

        @Test
        @DisplayName("Should handle null message")
        void shouldHandleNullMessage() {
            ApplicationException exception = new ApplicationException(null);
            assertNotNull(exception);
            assertNull(exception.getMessage());
        }

        @Test
        @DisplayName("Should handle empty message")
        void shouldHandleEmptyMessage() {
            String message = "";
            ApplicationException exception = new ApplicationException(message);
            
            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
        }

        @Test
        @DisplayName("Should be throwable and catchable")
        void shouldBeThrowableAndCatchable() {
            String message = "Test exception";
            
            ApplicationException thrownException = assertThrows(ApplicationException.class, () -> {
                throw new ApplicationException(message);
            });
            
            assertEquals(message, thrownException.getMessage());
        }

        @Test
        @DisplayName("Should maintain exception hierarchy")
        void shouldMaintainExceptionHierarchy() {
            ApplicationException exception = new ApplicationException("Test");
            
            assertTrue(exception instanceof Exception);
            assertTrue(exception instanceof Throwable);
        }
    }

    @Nested
    @DisplayName("DatabaseException Tests")
    class DatabaseExceptionTests {

        @Test
        @DisplayName("Should create DatabaseException with message")
        void shouldCreateDatabaseExceptionWithMessage() {
            String message = "Database connection failed";
            DatabaseException exception = new DatabaseException(message);

            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should create DatabaseException with message and cause")
        void shouldCreateDatabaseExceptionWithMessageAndCause() {
            String message = "Database operation failed";
            Exception cause = new RuntimeException("Connection timeout");
            DatabaseException exception = new DatabaseException(message, cause);

            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
            assertEquals(cause, exception.getCause());
        }

        @Test
        @DisplayName("Should handle typical database error scenarios")
        void shouldHandleTypicalDatabaseErrorScenarios() {
            // Connection failure
            DatabaseException connectionError = new DatabaseException("Failed to connect to DynamoDB");
            assertEquals("Failed to connect to DynamoDB", connectionError.getMessage());

            // Query failure
            Exception sqlCause = new RuntimeException("Invalid query syntax");
            DatabaseException queryError = new DatabaseException("Query execution failed", sqlCause);
            assertEquals("Query execution failed", queryError.getMessage());
            assertEquals(sqlCause, queryError.getCause());

            // Timeout
            DatabaseException timeoutError = new DatabaseException("Database operation timed out");
            assertEquals("Database operation timed out", timeoutError.getMessage());
        }

        @Test
        @DisplayName("Should be throwable and catchable")
        void shouldBeThrowableAndCatchable() {
            String message = "Database error";
            
            DatabaseException thrownException = assertThrows(DatabaseException.class, () -> {
                throw new DatabaseException(message);
            });
            
            assertEquals(message, thrownException.getMessage());
        }

        @Test
        @DisplayName("Should extend ApplicationException")
        void shouldExtendApplicationException() {
            DatabaseException exception = new DatabaseException("Test");
            
            assertTrue(exception instanceof ApplicationException);
            assertTrue(exception instanceof Exception);
            assertTrue(exception instanceof Throwable);
        }
    }

    @Nested
    @DisplayName("DeviceException Tests")
    class DeviceExceptionTests {

        @Test
        @DisplayName("Should create DeviceException with message")
        void shouldCreateDeviceExceptionWithMessage() {
            String message = "Device not found";
            DeviceException exception = new DeviceException(message);

            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should create DeviceException with message and cause")
        void shouldCreateDeviceExceptionWithMessageAndCause() {
            String message = "Device communication failed";
            Exception cause = new RuntimeException("Network timeout");
            DeviceException exception = new DeviceException(message, cause);

            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
            assertEquals(cause, exception.getCause());
        }

        @Test
        @DisplayName("Should handle typical device error scenarios")
        void shouldHandleTypicalDeviceErrorScenarios() {
            // Device not found
            DeviceException notFound = new DeviceException("Device with ID DEV001 not found");
            assertEquals("Device with ID DEV001 not found", notFound.getMessage());

            // Device offline
            DeviceException offline = new DeviceException("Device is offline");
            assertEquals("Device is offline", offline.getMessage());

            // Invalid operation
            Exception opCause = new IllegalStateException("Device is not powered on");
            DeviceException invalidOp = new DeviceException("Cannot perform operation", opCause);
            assertEquals("Cannot perform operation", invalidOp.getMessage());
            assertEquals(opCause, invalidOp.getCause());

            // Configuration error
            DeviceException configError = new DeviceException("Invalid device configuration");
            assertEquals("Invalid device configuration", configError.getMessage());
        }

        @Test
        @DisplayName("Should be throwable and catchable")
        void shouldBeThrowableAndCatchable() {
            String message = "Device error";
            
            DeviceException thrownException = assertThrows(DeviceException.class, () -> {
                throw new DeviceException(message);
            });
            
            assertEquals(message, thrownException.getMessage());
        }

        @Test
        @DisplayName("Should extend ApplicationException")
        void shouldExtendApplicationException() {
            DeviceException exception = new DeviceException("Test");
            
            assertTrue(exception instanceof ApplicationException);
            assertTrue(exception instanceof Exception);
            assertTrue(exception instanceof Throwable);
        }
    }

    @Nested
    @DisplayName("NetworkException Tests")
    class NetworkExceptionTests {

        @Test
        @DisplayName("Should create NetworkException with message")
        void shouldCreateNetworkExceptionWithMessage() {
            String message = "Network connection lost";
            NetworkException exception = new NetworkException(message);

            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should create NetworkException with message and cause")
        void shouldCreateNetworkExceptionWithMessageAndCause() {
            String message = "Failed to send data";
            Exception cause = new RuntimeException("Socket timeout");
            NetworkException exception = new NetworkException(message, cause);

            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
            assertEquals(cause, exception.getCause());
        }

        @Test
        @DisplayName("Should handle typical network error scenarios")
        void shouldHandleTypicalNetworkErrorScenarios() {
            // Connection timeout
            NetworkException timeout = new NetworkException("Connection timeout after 30 seconds");
            assertEquals("Connection timeout after 30 seconds", timeout.getMessage());

            // Host unreachable
            NetworkException unreachable = new NetworkException("Host unreachable");
            assertEquals("Host unreachable", unreachable.getMessage());

            // Socket error
            Exception socketCause = new RuntimeException("Socket closed unexpectedly");
            NetworkException socketError = new NetworkException("Socket communication failed", socketCause);
            assertEquals("Socket communication failed", socketError.getMessage());
            assertEquals(socketCause, socketError.getCause());

            // Protocol error
            NetworkException protocolError = new NetworkException("Invalid protocol version");
            assertEquals("Invalid protocol version", protocolError.getMessage());
        }

        @Test
        @DisplayName("Should be throwable and catchable")
        void shouldBeThrowableAndCatchable() {
            String message = "Network error";
            
            NetworkException thrownException = assertThrows(NetworkException.class, () -> {
                throw new NetworkException(message);
            });
            
            assertEquals(message, thrownException.getMessage());
        }

        @Test
        @DisplayName("Should extend ApplicationException")
        void shouldExtendApplicationException() {
            NetworkException exception = new NetworkException("Test");
            
            assertTrue(exception instanceof ApplicationException);
            assertTrue(exception instanceof Exception);
            assertTrue(exception instanceof Throwable);
        }
    }

    @Nested
    @DisplayName("UserException Tests")
    class UserExceptionTests {

        @Test
        @DisplayName("Should create UserException with message")
        void shouldCreateUserExceptionWithMessage() {
            String message = "Invalid user input";
            UserException exception = new UserException(message);

            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        @DisplayName("Should create UserException with message and cause")
        void shouldCreateUserExceptionWithMessageAndCause() {
            String message = "Validation failed";
            Exception cause = new IllegalArgumentException("Invalid format");
            UserException exception = new UserException(message, cause);

            assertNotNull(exception);
            assertEquals(message, exception.getMessage());
            assertEquals(cause, exception.getCause());
        }

        @Test
        @DisplayName("Should handle typical user input error scenarios")
        void shouldHandleTypicalUserInputErrorScenarios() {
            // Invalid input format
            UserException invalidFormat = new UserException("Date must be in YYYY-MM-DD format");
            assertEquals("Date must be in YYYY-MM-DD format", invalidFormat.getMessage());

            // Out of range
            UserException outOfRange = new UserException("Temperature must be between 17 and 30 degrees");
            assertEquals("Temperature must be between 17 and 30 degrees", outOfRange.getMessage());

            // Empty input
            UserException emptyInput = new UserException("Input cannot be empty");
            assertEquals("Input cannot be empty", emptyInput.getMessage());

            // Invalid characters
            Exception validationCause = new IllegalArgumentException("Contains invalid characters");
            UserException invalidChars = new UserException("Name can only contain letters and spaces", validationCause);
            assertEquals("Name can only contain letters and spaces", invalidChars.getMessage());
            assertEquals(validationCause, invalidChars.getCause());
        }

        @Test
        @DisplayName("Should be throwable and catchable")
        void shouldBeThrowableAndCatchable() {
            String message = "User input error";
            
            UserException thrownException = assertThrows(UserException.class, () -> {
                throw new UserException(message);
            });
            
            assertEquals(message, thrownException.getMessage());
        }

        @Test
        @DisplayName("Should extend ApplicationException")
        void shouldExtendApplicationException() {
            UserException exception = new UserException("Test");
            
            assertTrue(exception instanceof ApplicationException);
            assertTrue(exception instanceof Exception);
            assertTrue(exception instanceof Throwable);
        }
    }

    @Nested
    @DisplayName("Exception Hierarchy Tests")
    class ExceptionHierarchyTests {

        @Test
        @DisplayName("Should maintain proper inheritance hierarchy")
        void shouldMaintainProperInheritanceHierarchy() {
            ApplicationException appException = new ApplicationException("App error");
            DatabaseException dbException = new DatabaseException("DB error");
            DeviceException deviceException = new DeviceException("Device error");
            NetworkException networkException = new NetworkException("Network error");
            UserException userException = new UserException("User error");

            // All specific exceptions should extend ApplicationException
            assertTrue(dbException instanceof ApplicationException);
            assertTrue(deviceException instanceof ApplicationException);
            assertTrue(networkException instanceof ApplicationException);
            assertTrue(userException instanceof ApplicationException);

            // All exceptions should extend Exception
            assertTrue(appException instanceof Exception);
            assertTrue(dbException instanceof Exception);
            assertTrue(deviceException instanceof Exception);
            assertTrue(networkException instanceof Exception);
            assertTrue(userException instanceof Exception);
        }

        @Test
        @DisplayName("Should be distinguishable by type")
        void shouldBeDistinguishableByType() {
            Exception[] exceptions = {
                new ApplicationException("App error"),
                new DatabaseException("DB error"),
                new DeviceException("Device error"),
                new NetworkException("Network error"),
                new UserException("User error")
            };

            // Each exception should be identifiable by its specific type
            assertTrue(exceptions[0] instanceof ApplicationException);
            assertFalse(exceptions[0] instanceof DatabaseException);
            
            assertTrue(exceptions[1] instanceof DatabaseException);
            assertTrue(exceptions[1] instanceof ApplicationException);
            
            assertTrue(exceptions[2] instanceof DeviceException);
            assertTrue(exceptions[2] instanceof ApplicationException);
            
            assertTrue(exceptions[3] instanceof NetworkException);
            assertTrue(exceptions[3] instanceof ApplicationException);
            
            assertTrue(exceptions[4] instanceof UserException);
            assertTrue(exceptions[4] instanceof ApplicationException);
        }
    }

    @Nested
    @DisplayName("Exception Handling Patterns Tests")
    class ExceptionHandlingPatternsTests {

        @Test
        @DisplayName("Should support chained exception handling")
        void shouldSupportChainedExceptionHandling() {
            Exception rootCause = new RuntimeException("Root cause");
            Exception middleException = new IllegalStateException("Middle layer", rootCause);
            ApplicationException topException = new ApplicationException("Top level error", middleException);

            assertEquals("Top level error", topException.getMessage());
            assertEquals(middleException, topException.getCause());
            assertEquals(rootCause, topException.getCause().getCause());
        }

        @Test
        @DisplayName("Should support exception wrapping pattern")
        void shouldSupportExceptionWrappingPattern() {
            // Simulate wrapping low-level exceptions in domain-specific exceptions
            RuntimeException lowLevelError = new RuntimeException("Low level system error");
            
            DatabaseException dbWrapper = new DatabaseException("Database operation failed", lowLevelError);
            DeviceException deviceWrapper = new DeviceException("Device communication failed", lowLevelError);
            NetworkException networkWrapper = new NetworkException("Network transmission failed", lowLevelError);
            UserException userWrapper = new UserException("Invalid user input detected", lowLevelError);

            // All should wrap the same low-level error but provide domain-specific context
            assertEquals(lowLevelError, dbWrapper.getCause());
            assertEquals(lowLevelError, deviceWrapper.getCause());
            assertEquals(lowLevelError, networkWrapper.getCause());
            assertEquals(lowLevelError, userWrapper.getCause());

            // Each should have its own contextual message
            assertTrue(dbWrapper.getMessage().contains("Database"));
            assertTrue(deviceWrapper.getMessage().contains("Device"));
            assertTrue(networkWrapper.getMessage().contains("Network"));
            assertTrue(userWrapper.getMessage().contains("user input"));
        }

        @Test
        @DisplayName("Should support multi-catch exception handling")
        void shouldSupportMultiCatchExceptionHandling() {
            // Test that exceptions can be caught at different levels of hierarchy
            ApplicationException specificException = new DatabaseException("Specific DB error");
            
            // Can be caught as specific type
            assertThrows(DatabaseException.class, () -> {
                throw (DatabaseException) specificException;
            });

            // Can be caught as base application exception
            ApplicationException caughtAsBase = assertThrows(ApplicationException.class, () -> {
                throw specificException;
            });
            
            assertEquals("Specific DB error", caughtAsBase.getMessage());
        }

        @Test
        @DisplayName("Should preserve stack trace information")
        void shouldPreserveStackTraceInformation() {
            try {
                throwNestedExceptions();
                fail("Should have thrown an exception");
            } catch (ApplicationException e) {
                assertNotNull(e.getStackTrace());
                assertTrue(e.getStackTrace().length > 0);
                
                // Should contain information about where exception was thrown
                StackTraceElement[] stackTrace = e.getStackTrace();
                boolean foundThrowingMethod = false;
                for (StackTraceElement element : stackTrace) {
                    if (element.getMethodName().equals("throwNestedExceptions")) {
                        foundThrowingMethod = true;
                        break;
                    }
                }
                assertTrue(foundThrowingMethod, "Stack trace should contain the method that threw the exception");
            }
        }

        private void throwNestedExceptions() throws ApplicationException {
            try {
                throw new RuntimeException("Inner exception");
            } catch (RuntimeException e) {
                throw new ApplicationException("Wrapped exception", e);
            }
        }
    }

    @Nested
    @DisplayName("Exception Message and Localization Tests")
    class ExceptionMessageTests {

        @Test
        @DisplayName("Should handle various message formats")
        void shouldHandleVariousMessageFormats() {
            // Simple messages
            ApplicationException simple = new ApplicationException("Simple error");
            assertEquals("Simple error", simple.getMessage());

            // Messages with parameters (formatted strings)
            String deviceId = "DEV001";
            DeviceException withParams = new DeviceException("Device " + deviceId + " not found");
            assertEquals("Device DEV001 not found", withParams.getMessage());

            // Multi-line messages
            String multiLine = "Error occurred:\nLine 1\nLine 2";
            UserException multiLineEx = new UserException(multiLine);
            assertEquals(multiLine, multiLineEx.getMessage());

            // Messages with special characters
            String specialChars = "Error: @#$%^&*()_+-=[]{}|;':\",./<>?";
            NetworkException specialEx = new NetworkException(specialChars);
            assertEquals(specialChars, specialEx.getMessage());
        }

        @Test
        @DisplayName("Should handle extremely long messages")
        void shouldHandleExtremelyLongMessages() {
            String longMessage = "Error: " + "A".repeat(10000);
            ApplicationException longEx = new ApplicationException(longMessage);
            
            assertEquals(longMessage.length(), longEx.getMessage().length());
            assertTrue(longEx.getMessage().startsWith("Error: AAAA"));
        }

        @Test
        @DisplayName("Should handle Unicode and international characters")
        void shouldHandleUnicodeAndInternationalCharacters() {
            String unicodeMessage = "Error: 温度设置失败 🌡️ température incorrecte";
            UserException unicodeEx = new UserException(unicodeMessage);
            
            assertEquals(unicodeMessage, unicodeEx.getMessage());
        }
    }
}