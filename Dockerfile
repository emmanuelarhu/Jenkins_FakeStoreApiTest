# Multi-stage Dockerfile for FakeStore API Test Automation

# Stage 1: Build and Test Environment
FROM maven:3.8.6-openjdk-17-slim AS test-environment

# Set maintainer information
LABEL maintainer="Emmanuel Arhu <emmanuel@example.com>"
LABEL description="FakeStore API Test Automation with REST Assured"
LABEL version="1.0"

# Set working directory
WORKDIR /FakeStoreApiTest

# Install additional tools for testing
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    git \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Create directories for reports and logs
RUN mkdir -p /FakeStoreApiTest/target/allure-results \
    && mkdir -p /FakeStoreApiTest/target/surefire-reports \
    && mkdir -p /FakeStoreApiTest/target/logs

# Copy Maven configuration first (for better layer caching)
COPY pom.xml ./

# Download Maven dependencies (cached layer if pom.xml doesn't change)
RUN mvn dependency:go-offline -B --no-transfer-progress

# Copy project source code
COPY src ./src
COPY testng.xml ./
COPY allure.properties ./

# Set environment variables
ENV MAVEN_OPTS="-Xmx2048m -Xms1024m"
ENV API_BASE_URL="https://fakestoreapi.com"
ENV ALLURE_RESULTS_DIR="/FakeStoreApiTest/target/allure-results"

# Compile the project
RUN mvn clean compile test-compile -DskipTests=true --no-transfer-progress

# Stage 2: Test Execution (Production-like)
FROM test-environment AS test-runner

# Health check to verify API connectivity
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f ${API_BASE_URL}/users/1 || exit 1

# Expose port for potential web-based reporting
EXPOSE 8080

# Create entrypoint script
RUN cat > /FakeStoreApiTest/run-tests.sh << 'EOF'
#!/bin/bash
set -e

echo "🚀 Starting FakeStore API Test Execution"
echo "================================================"
echo "Base URL: ${API_BASE_URL}"
echo "Test Suite: ${TEST_SUITE:-all}"
echo "Environment: ${ENVIRONMENT:-production}"
echo "Maven Options: ${MAVEN_OPTS}"
echo "================================================"

# Verify API connectivity
echo "🔍 Checking API connectivity..."
if curl -s -f ${API_BASE_URL}/users/1 > /dev/null; then
    echo "✅ API is accessible"
else
    echo "⚠️ API connectivity check failed, but continuing with tests..."
fi

# Determine test command based on TEST_SUITE
case "${TEST_SUITE:-all}" in
    "smoke")
        echo "💨 Running Smoke Tests..."
        mvn test -Dgroups="smoke" -Dmaven.test.failure.ignore=true --no-transfer-progress
        ;;
    "security")
        echo "🔒 Running Security Tests..."
        mvn test -Dgroups="security" -Dmaven.test.failure.ignore=true --no-transfer-progress
        ;;
    "performance")
        echo "⚡ Running Performance Tests..."
        mvn test -Dgroups="performance" -Dmaven.test.failure.ignore=true --no-transfer-progress
        ;;
    "regression")
        echo "🔄 Running Regression Tests..."
        mvn test -Dmaven.test.failure.ignore=true --no-transfer-progress
        ;;
    *)
        echo "🎯 Running All Tests..."
        mvn test -Dmaven.test.failure.ignore=true --no-transfer-progress
        ;;
esac

# Generate Allure report if results exist
if [ -d "${ALLURE_RESULTS_DIR}" ] && [ "$(ls -A ${ALLURE_RESULTS_DIR})" ]; then
    echo "📊 Generating Allure report..."
    mvn allure:report --no-transfer-progress
    echo "✅ Allure report generated"
else
    echo "⚠️ No Allure results found"
fi

# Display test summary
echo "================================================"
echo "📋 Test Execution Summary"
echo "================================================"

if [ -d "target/surefire-reports" ]; then
    XML_COUNT=$(find target/surefire-reports -name "*.xml" | wc -l)
    echo "📄 Generated ${XML_COUNT} test report files"

    if [ ${XML_COUNT} -gt 0 ]; then
        echo "Test Results:"
        find target/surefire-reports -name "TEST-*.xml" | while read file; do
            echo "  $(basename $file)"
        done
    fi
else
    echo "⚠️ No test reports found"
fi

echo "================================================"
echo "🎉 Test execution completed!"
echo "================================================"
EOF

# Make the script executable
RUN chmod +x /FakeStoreApiTest/run-tests.sh

# Default command
CMD ["/FakeStoreApiTest/run-tests.sh"]

# Alternative commands for different test scenarios
# docker run fakestore-api-tests                    # Run all tests
# docker run -e TEST_SUITE=smoke fakestore-api-tests    # Run smoke tests
# docker run -e TEST_SUITE=security fakestore-api-tests # Run security tests

# Stage 3: Report Server (Optional - for serving reports)
FROM nginx:alpine AS report-server

# Copy generated reports from test stage
COPY --from=test-runner /FakeStoreApiTest/target/site/allure-maven-plugin /usr/share/nginx/html/allure
COPY --from=test-runner /FakeStoreApiTest/target/surefire-reports /usr/share/nginx/html/surefire

# Create index page for easy navigation
RUN cat > /usr/share/nginx/html/index.html << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <title>FakeStore API Test Reports</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
        .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .header { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 20px; margin-bottom: 30px; }
        .links a { display: inline-block; margin: 10px 20px 10px 0; padding: 10px 20px; background: #3498db; color: white; text-decoration: none; border-radius: 5px; }
        .links a:hover { background: #2980b9; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🧪 FakeStore API Test Reports</h1>
            <p>Automated API Testing Results</p>
        </div>
        <div class="links">
            <a href="/allure/">📈 Allure Report</a>
            <a href="/surefire/">📊 Surefire Reports</a>
        </div>
        <p><strong>API Tested:</strong> https://fakestoreapi.com/users</p>
        <p><strong>Framework:</strong> REST Assured + TestNG</p>
        <p><strong>Generated:</strong> <span id="datetime"></span></p>
    </div>
    <script>
        document.getElementById('datetime').textContent = new Date().toLocaleString();
    </script>
</body>
</html>
EOF

EXPOSE 80

# Build Instructions:
# docker build -t fakestore-api-tests .
# docker build --target test-runner -t fakestore-api-tests:test .
# docker build --target report-server -t fakestore-api-tests:reports .

# Run Instructions:
# docker run --rm fakestore-api-tests                           # Run all tests
# docker run --rm -e TEST_SUITE=smoke fakestore-api-tests      # Run specific suite
# docker run -p 8080:80 fakestore-api-tests:reports            # Serve reports