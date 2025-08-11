# Dockerfile for FakeStore API Tests with Allure support
FROM maven:3.8.1-openjdk-17-slim

# Install necessary packages
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Install Allure
RUN curl -o allure-2.24.0.tgz -Ls https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/2.24.0/allure-commandline-2.24.0.tgz && \
    tar -zxvf allure-2.24.0.tgz -C /opt/ && \
    ln -s /opt/allure-2.24.0/bin/allure /usr/bin/allure && \
    rm allure-2.24.0.tgz

# Set working directory
WORKDIR /FakeStoreApiTest

# Copy project files
COPY pom.xml .
COPY src ./src
COPY testng.xml .

# Install dependencies
RUN mvn dependency:go-offline

# Default command
CMD ["mvn", "clean", "test"]