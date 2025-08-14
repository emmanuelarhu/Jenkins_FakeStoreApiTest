# FakeStore API - Users Testing Framework

A comprehensive API testing framework for the FakeStore API Users endpoints using REST Assured, TestNG, and Allure reporting.

## 🚀 Features
 
- **Complete CRUD Testing**: Tests for GET, POST, PUT, DELETE operations
- **Comprehensive Coverage**: Positive, negative, and edge test cases
- **Security Testing**: SQL injection and XSS protection validation
- **Performance Testing**: Response time and concurrent request handling
- **Data Validation**: Email format and input validation tests
- **Beautiful Reports**: Allure reporting with detailed test documentation
- **Parameterized Tests**: Data-driven testing using TestNG DataProviders

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Internet connection (for API calls)

## 🛠️ Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/emmanuelarhu/FakeStoreApiTest.git
   cd FakeStoreApiTest
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

## 🧪 Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Suite
```bash
# Smoke tests only
mvn test -DtestSuite=Smoke-Tests

# Regression tests
mvn test -DtestSuite=Regression-Tests
```

### Run with TestNG XML
```bash
mvn test -DsuiteXmlFile=testng.xml
```

## 📊 Generating Reports

### Allure Reports
```bash
# Generate and serve Allure report
mvn allure:serve

# Generate Allure report only
mvn allure:report
```

The Allure report will be available at `target/site/allure-maven-plugin/index.html`

## 📁 Project Structure

```
src/
├── test/
│   └── java/
│       └── com/
│           └── emmanuelarhu/
│               ├── base/
│               │   └── BaseTest.java          # Base test configuration
│               ├── models/
│               │   └── User.java              # User model/POJO
│               ├── tests/
│               │   └── UsersApiTest.java      # Main test class
│               └── utils/
│                   └── TestDataProvider.java # Test data provider
├── testng.xml                                 # TestNG configuration
└── pom.xml                                   # Maven configuration
```

## 🎯 Test Coverage

### API Endpoints Tested
- `GET /users` - Retrieve all users
- `GET /users/{id}` - Retrieve specific user
- `POST /users` - Create new user
- `PUT /users/{id}` - Update existing user
- `DELETE /users/{id}` - Delete user

### Test Categories

#### ✅ Positive Tests
- Valid user creation with proper data
- Successful retrieval of existing users
- Proper user updates and deletions
- Response time validation

#### ❌ Negative Tests
- Invalid user IDs (negative, non-existent, non-numeric)
- Invalid user data (missing fields, invalid email formats)
- Malformed JSON requests
- Empty request bodies

#### 🔒 Security Tests
- SQL injection protection
- XSS (Cross-Site Scripting) protection
- Input sanitization validation

#### ⚡ Performance Tests
- Response time thresholds
- Concurrent request handling

## 🏷️ Test Annotations

Tests are organized using Allure annotations:
- `@Epic`: FakeStore API Testing
- `@Feature`: Users Management
- `@Story`: Specific functionality (Get Users, Create User, etc.)
- `@Severity`: Test importance level

## 📈 Test Execution Priority

Tests are executed in priority order:
1. **Priority 1-6**: GET operations and basic validations
2. **Priority 7-10**: POST operations and user creation
3. **Priority 11-14**: PUT operations and user updates
4. **Priority 15-18**: DELETE operations
5. **Priority 19-22**: Security and edge case tests

## 🔧 Configuration

### Base URL
The base URL is configured in `BaseTest.java`:
```java
protected static final String BASE_URL = "https://fakestoreapi.com";
```

### Request Specifications
- All requests include Allure reporting filters
- JSON content-type headers for POST/PUT operations
- Automatic request/response logging on failures

## 📝 Test Data

Test data is managed through `TestDataProvider.java`:
- Valid user data for creation tests
- Invalid data for negative testing
- User IDs for parameterized testing
- Update scenarios for modification tests

## 🚨 Error Handling

The framework handles various error scenarios:
- Network timeouts and connectivity issues
- Invalid JSON responses
- HTTP error codes (400, 404, 422, etc.)
- Assertion failures with detailed reporting

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests following the existing patterns
4. Ensure all tests pass
5. Submit a pull request

## 📚 Dependencies

- **REST Assured**: API testing framework
- **TestNG**: Test execution and organization
- **Allure**: Test reporting and documentation
- **Jackson**: JSON processing
- **SLF4J**: Logging framework
- **AspectJ**: AOP support for Allure

## 🐛 Troubleshooting

### Common Issues

1. **Tests failing due to network issues**
    - Check internet connectivity
    - Verify FakeStore API is accessible

2. **Allure reports not generating**
    - Ensure AspectJ weaver is properly configured
    - Check Maven Surefire plugin configuration

3. **TestNG tests not discovered**
    - Verify test class naming conventions (*Test.java)
    - Check TestNG dependencies in pom.xml

## 📞 Support

For questions or issues, please contact:
- **Author**: Emmanuel Arhu
- **GitHub**: [github.com/emmanuelarhu](https://github.com/emmanuelarhu)
- **LinkedIn**: [linkedin.com/in/emmanuelarhu](https://www.linkedin.com/in/emmanuelarhu)

---

**Happy Testing! 🎉**