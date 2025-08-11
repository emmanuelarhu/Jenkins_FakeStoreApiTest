# Simple Dockerfile for FakeStore API Tests
FROM maven:3.8.1-openjdk-17-slim

# Set working directory
WORKDIR /FakeStoreApiTest

# Copy project files
COPY pom.xml .
COPY src ./src
COPY testng.xml .

# Install dependencies
RUN mvn dependency:go-offline

# Run tests
CMD ["mvn", "clean", "test"]