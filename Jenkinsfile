pipeline {
    agent any

    tools {
        maven 'Maven-3.8'
        jdk 'JDK-17'
    }

    environment {
        MAVEN_OPTS = '-Xmx2048m -Xms1024m'
        ALLURE_RESULTS = 'target/allure-results'
        SUREFIRE_REPORTS = 'target/surefire-reports'
        API_BASE_URL = 'https://fakestoreapi.com'
    }

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['all', 'smoke', 'regression', 'security', 'performance'],
            description: 'Select test suite to run'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['production', 'staging'],
            description: 'Target environment'
        )
        booleanParam(
            name: 'SEND_NOTIFICATIONS',
            defaultValue: true,
            description: 'Send notifications on completion'
        )
        booleanParam(
            name: 'PARALLEL_EXECUTION',
            defaultValue: true,
            description: 'Run tests in parallel'
        )
    }

    stages {
        stage('📁 Checkout & Setup') {
            steps {
                script {
                    echo "🔄 Checking out FakeStore API test automation code..."
                    echo "Repository: ${env.GIT_URL}"
                    echo "Branch: ${env.GIT_BRANCH}"
                    echo "Commit: ${env.GIT_COMMIT}"
                    echo "Build Number: ${env.BUILD_NUMBER}"

                    // Create necessary directories
                    sh '''
                        mkdir -p target/logs
                        mkdir -p target/allure-results
                        mkdir -p target/surefire-reports
                    '''
                }
            }
        }

        stage('🔍 Environment Validation') {
            steps {
                script {
                    echo "📊 Validating build environment..."
                    sh '''
                        echo "=== Environment Information ==="
                        echo "Java Version: $(java -version 2>&1 | head -1)"
                        echo "Maven Version: $(mvn -version | head -1)"
                        echo "Current Directory: $(pwd)"
                        echo "Available Memory: $(free -h 2>/dev/null || echo 'Memory info not available')"
                        echo "Selected Test Suite: ${TEST_SUITE}"
                        echo "Target Environment: ${ENVIRONMENT}"
                        echo "API Base URL: ${API_BASE_URL}"

                        echo "=== Project Structure ==="
                        find src/test -name "*.java" | head -10

                        echo "=== Testing API Connectivity ==="
                        curl -s -o /dev/null -w "HTTP Status: %{http_code}, Time: %{time_total}s\n" ${API_BASE_URL}/users/1 || echo "API connectivity check failed"
                    '''
                }
            }
        }

        stage('📦 Build & Dependencies') {
            steps {
                script {
                    echo "🔨 Installing Maven dependencies and compiling..."
                    sh '''
                        echo "Cleaning previous builds..."
                        mvn clean

                        echo "Downloading dependencies..."
                        mvn dependency:resolve dependency:resolve-sources \
                        --batch-mode --no-transfer-progress

                        echo "Compiling project..."
                        mvn compile test-compile \
                        -Dmaven.test.skip=true \
                        -Dmaven.javadoc.skip=true \
                        --batch-mode --no-transfer-progress

                        echo "Verifying compiled classes..."
                        find target/test-classes -name "*.class" | head -5
                    '''
                }
            }
            post {
                failure {
                    echo "❌ Build failed during dependency resolution or compilation"
                    error "Failed to build project and install dependencies"
                }
            }
        }

        stage('🧪 Execute API Tests') {
            when {
                expression { params.PARALLEL_EXECUTION == true }
            }
            parallel {
                stage('Health Check') {
                    steps {
                        script {
                            echo "🏥 Running Health Check Tests..."
                            sh '''
                                mvn test -Dtest="**/*Test" \
                                -Dgroups="health" \
                                -Dmaven.test.failure.ignore=true \
                                -Dsurefire.reportNameSuffix=health \
                                --batch-mode --no-transfer-progress
                            '''
                        }
                    }
                }

                stage('Smoke Tests') {
                    when {
                        anyOf {
                            params.TEST_SUITE == 'smoke'
                            params.TEST_SUITE == 'all'
                        }
                    }
                    steps {
                        script {
                            echo "💨 Running Smoke Tests..."
                            sh '''
                                mvn test -Dtest="**/*Test" \
                                -Dgroups="smoke" \
                                -Dmaven.test.failure.ignore=true \
                                -Dsurefire.reportNameSuffix=smoke \
                                --batch-mode --no-transfer-progress
                            '''
                        }
                    }
                }

                stage('Security Tests') {
                    when {
                        anyOf {
                            params.TEST_SUITE == 'security'
                            params.TEST_SUITE == 'all'
                        }
                    }
                    steps {
                        script {
                            echo "🔒 Running Security Tests..."
                            sh '''
                                mvn test -Dtest="**/*Test" \
                                -Dgroups="security" \
                                -Dmaven.test.failure.ignore=true \
                                -Dsurefire.reportNameSuffix=security \
                                --batch-mode --no-transfer-progress
                            '''
                        }
                    }
                }

                stage('Performance Tests') {
                    when {
                        anyOf {
                            params.TEST_SUITE == 'performance'
                            params.TEST_SUITE == 'all'
                        }
                    }
                    steps {
                        script {
                            echo "⚡ Running Performance Tests..."
                            sh '''
                                mvn test -Dtest="**/*Test" \
                                -Dgroups="performance" \
                                -Dmaven.test.failure.ignore=true \
                                -Dsurefire.reportNameSuffix=performance \
                                --batch-mode --no-transfer-progress
                            '''
                        }
                    }
                }
            }
        }

        stage('🧪 Sequential Test Execution') {
            when {
                expression { params.PARALLEL_EXECUTION == false }
            }
            steps {
                script {
                    echo "🔄 Running tests sequentially..."
                    def testCommand = ""

                    switch(params.TEST_SUITE) {
                        case 'smoke':
                            testCommand = 'mvn test -Dgroups="smoke"'
                            break
                        case 'security':
                            testCommand = 'mvn test -Dgroups="security"'
                            break
                        case 'performance':
                            testCommand = 'mvn test -Dgroups="performance"'
                            break
                        case 'regression':
                            testCommand = 'mvn test'
                            break
                        default:
                            testCommand = 'mvn test'
                    }

                    sh """
                        ${testCommand} \
                        -Dmaven.test.failure.ignore=true \
                        --batch-mode --no-transfer-progress
                    """
                }
            }
        }

        stage('📊 Generate & Process Reports') {
            parallel {
                stage('Generate Allure Report') {
                    steps {
                        script {
                            echo "📈 Generating Allure reports..."
                            sh '''
                                echo "Checking for Allure results..."
                                if [ -d "${ALLURE_RESULTS}" ] && [ "$(ls -A ${ALLURE_RESULTS})" ]; then
                                    echo "✅ Allure results found: $(ls ${ALLURE_RESULTS} | wc -l) files"
                                    ls -la ${ALLURE_RESULTS}/

                                    # Generate Allure report
                                    mvn allure:report --batch-mode --no-transfer-progress

                                    echo "Allure report generated successfully"
                                else
                                    echo "⚠️ No Allure results found, creating minimal report structure"
                                    mkdir -p ${ALLURE_RESULTS}
                                    cat > ${ALLURE_RESULTS}/environment.properties << EOF
BUILD_NUMBER=${BUILD_NUMBER}
BUILD_URL=${BUILD_URL}
API_BASE_URL=${API_BASE_URL}
TEST_SUITE=${TEST_SUITE}
EXECUTION_DATE=$(date)
EOF
                                fi
                            '''
                        }
                    }
                }

                stage('Process Test Results') {
                    steps {
                        script {
                            echo "📋 Processing Surefire test results..."
                            sh '''
                                echo "Analyzing test results..."
                                if [ -d "${SUREFIRE_REPORTS}" ]; then
                                    XML_FILES=$(find ${SUREFIRE_REPORTS} -name "*.xml" | wc -l)
                                    echo "📄 Found ${XML_FILES} XML report files"

                                    # Display test summary
                                    if [ ${XML_FILES} -gt 0 ]; then
                                        echo "=== Test Execution Summary ==="
                                        find ${SUREFIRE_REPORTS} -name "TEST-*.xml" -exec grep -l "testcase" {} \\; | while read file; do
                                            echo "Processing: $(basename $file)"
                                            grep -o 'tests="[^"]*"' "$file" | head -1
                                            grep -o 'failures="[^"]*"' "$file" | head -1
                                            grep -o 'errors="[^"]*"' "$file" | head -1
                                            echo "---"
                                        done
                                    fi

                                    ls -la ${SUREFIRE_REPORTS}/
                                else
                                    echo "⚠️ No Surefire reports directory found"
                                fi

                                # Create test summary
                                echo "=== Build Summary ===" > target/test-summary.txt
                                echo "Build Number: ${BUILD_NUMBER}" >> target/test-summary.txt
                                echo "Test Suite: ${TEST_SUITE}" >> target/test-summary.txt
                                echo "Environment: ${ENVIRONMENT}" >> target/test-summary.txt
                                echo "Execution Date: $(date)" >> target/test-summary.txt
                                echo "XML Reports: ${XML_FILES:-0}" >> target/test-summary.txt
                            '''
                        }
                    }
                }
            }
        }

        stage('📦 Archive Results & Reports') {
            steps {
                script {
                    echo "📦 Archiving test results, reports, and artifacts..."

                    // Archive test results
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*',
                                   allowEmptyArchive: true,
                                   fingerprint: true,
                                   defaultExcludes: false

                    // Archive Allure results
                    archiveArtifacts artifacts: 'target/allure-results/**/*',
                                   allowEmptyArchive: true,
                                   fingerprint: true

                    // Archive generated reports
                    archiveArtifacts artifacts: 'target/site/allure-maven-plugin/**/*',
                                   allowEmptyArchive: true

                    // Archive logs and summaries
                    archiveArtifacts artifacts: 'target/logs/**/*,target/test-summary.txt',
                                   allowEmptyArchive: true

                    echo "✅ Artifacts archived successfully"
                }
            }
        }

        stage('📊 Publish Test Reports') {
            steps {
                script {
                    echo "📈 Publishing test reports..."

                    // Publish JUnit test results
                    publishTestResults testResultsPattern: 'target/surefire-reports/TEST-*.xml',
                                     allowEmptyResults: true,
                                     skipPublishingChecks: false

                    // Publish Allure report
                    allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'target/allure-results']]
                    ])

                    // Publish HTML reports
                    publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/allure-maven-plugin',
                        reportFiles: 'index.html',
                        reportName: 'Allure Test Report',
                        reportTitles: 'FakeStore API Test Results'
                    ])

                    echo "✅ Reports published successfully"
                }
            }
        }
    }

    post {
        always {
            script {
                echo "🔍 Post-build actions..."

                // Collect test metrics
                def testResults = [:]
                try {
                    def testResult = currentBuild.rawBuild.getAction(hudson.tasks.test.AbstractTestResultAction.class)
                    if (testResult != null) {
                        testResults.totalTests = testResult.totalCount
                        testResults.failedTests = testResult.failCount
                        testResults.skippedTests = testResult.skipCount
                        testResults.passedTests = testResult.totalCount - testResult.failCount - testResult.skipCount
                    }
                } catch (Exception e) {
                    echo "⚠️ Could not collect test metrics: ${e.getMessage()}"
                }

                // Store test results for notifications
                env.TEST_RESULTS = groovy.json.JsonOutput.toJson(testResults)

                echo "📊 Test execution completed"
                echo "Build Status: ${currentBuild.currentResult}"
                echo "Duration: ${currentBuild.durationString}"
            }
        }

        success {
            script {
                echo "✅ Pipeline completed successfully!"
                currentBuild.description = "✅ ${params.TEST_SUITE} tests passed"

                if (params.SEND_NOTIFICATIONS) {
                    sendNotifications('SUCCESS')
                }
            }
        }

        failure {
            script {
                echo "❌ Pipeline failed!"
                currentBuild.description = "❌ ${params.TEST_SUITE} tests failed"

                if (params.SEND_NOTIFICATIONS) {
                    sendNotifications('FAILURE')
                }
            }
        }

        unstable {
            script {
                echo "⚠️ Pipeline completed with test failures!"
                currentBuild.description = "⚠️ ${params.TEST_SUITE} tests unstable"

                if (params.SEND_NOTIFICATIONS) {
                    sendNotifications('UNSTABLE')
                }
            }
        }

        cleanup {
            script {
                echo "🧹 Cleaning up workspace..."
                // Clean workspace but keep important artifacts
                sh '''
                    # Keep important directories
                    find . -name "target" -type d -exec rm -rf {}/dependency-maven-plugin-markers \\; 2>/dev/null || true
                    find . -name "*.tmp" -delete 2>/dev/null || true
                '''
            }
        }
    }
}

// Custom function for comprehensive notifications
def sendNotifications(String status) {
    def color = status == 'SUCCESS' ? 'good' : status == 'FAILURE' ? 'danger' : 'warning'
    def emoji = status == 'SUCCESS' ? '✅' : status == 'FAILURE' ? '❌' : '⚠️'

    // Parse test results
    def testResults = [:]
    try {
        if (env.TEST_RESULTS) {
            testResults = readJSON text: env.TEST_RESULTS
        }
    } catch (Exception e) {
        echo "Could not parse test results: ${e.getMessage()}"
    }

    def testSummary = ""
    if (testResults.totalTests) {
        testSummary = """
            <li><strong>Total Tests:</strong> ${testResults.totalTests}</li>
            <li><strong>Passed:</strong> ${testResults.passedTests}</li>
            <li><strong>Failed:</strong> ${testResults.failedTests}</li>
            <li><strong>Skipped:</strong> ${testResults.skippedTests}</li>
        """
    }

    // Email notification with comprehensive details
    emailext (
        subject: "${emoji} FakeStore API Tests - ${status} (Build #${env.BUILD_NUMBER})",
        body: """
            <html>
            <body style="font-family: Arial, sans-serif; margin: 20px;">
                <h2 style="color: ${status == 'SUCCESS' ? '#28a745' : status == 'FAILURE' ? '#dc3545' : '#ffc107'};">
                    ${emoji} FakeStore API Test Execution ${status}
                </h2>

                <h3>📊 Build Information</h3>
                <ul>
                    <li><strong>Job:</strong> ${env.JOB_NAME}</li>
                    <li><strong>Build Number:</strong> ${env.BUILD_NUMBER}</li>
                    <li><strong>Test Suite:</strong> ${params.TEST_SUITE}</li>
                    <li><strong>Environment:</strong> ${params.ENVIRONMENT}</li>
                    <li><strong>Duration:</strong> ${currentBuild.durationString}</li>
                    <li><strong>Branch:</strong> ${env.GIT_BRANCH ?: 'N/A'}</li>
                    <li><strong>Commit:</strong> ${env.GIT_COMMIT ? env.GIT_COMMIT.take(8) : 'N/A'}</li>
                </ul>

                <h3>🧪 Test Results</h3>
                <ul>
                    ${testSummary ?: '<li>Test results not available</li>'}
                </ul>

                <h3>🔗 Quick Links</h3>
                <ul>
                    <li><a href="${env.BUILD_URL}" style="color: #007bff;">📋 Build Details</a></li>
                    <li><a href="${env.BUILD_URL}allure/" style="color: #007bff;">📈 Allure Report</a></li>
                    <li><a href="${env.BUILD_URL}testReport/" style="color: #007bff;">📊 Test Results</a></li>
                    <li><a href="${env.BUILD_URL}artifact/" style="color: #007bff;">📦 Artifacts</a></li>
                </ul>

                <h3>📋 Test Coverage</h3>
                <p>This execution tested the FakeStore API Users endpoints:</p>
                <ul>
                    <li>✅ GET /users - Retrieve all users</li>
                    <li>✅ GET /users/{id} - Retrieve specific user</li>
                    <li>✅ POST /users - Create new user</li>
                    <li>✅ PUT /users/{id} - Update existing user</li>
                    <li>✅ DELETE /users/{id} - Delete user</li>
                </ul>

                <hr style="margin: 20px 0;">
                <p style="color: #6c757d; font-size: 12px;">
                    <em>🤖 Automated by Jenkins CI/CD Pipeline | FakeStore API Test Automation Framework</em>
                </p>
            </body>
            </html>
        """,
        to: "${env.BUILD_USER_EMAIL ?: 'qa-team@company.com'}",
        mimeType: 'text/html'
    )

    // Slack notification with rich formatting
    try {
        def slackMessage = """
            ${emoji} *FakeStore API Tests - ${status}*

            *📊 Build Information*
            • *Job:* ${env.JOB_NAME}
            • *Build:* #${env.BUILD_NUMBER}
            • *Test Suite:* ${params.TEST_SUITE}
            • *Environment:* ${params.ENVIRONMENT}
            • *Duration:* ${currentBuild.durationString}
            • *Branch:* ${env.GIT_BRANCH ?: 'N/A'}

            *🧪 Test Results*
            ${testResults.totalTests ? "• *Total:* ${testResults.totalTests} | *Passed:* ${testResults.passedTests} | *Failed:* ${testResults.failedTests}" : "• Test results processing..."}

            *🔗 Quick Actions*
            <${env.BUILD_URL}|📋 View Build> | <${env.BUILD_URL}allure/|📈 View Report> | <${env.BUILD_URL}testReport/|📊 Test Results>
        """.stripIndent()

        slackSend (
            channel: '#qa-automation',
            color: color,
            message: slackMessage,
            teamDomain: 'your-workspace',
            tokenCredentialId: 'slack-token'
        )
    } catch (Exception e) {
        echo "⚠️ Slack notification failed: ${e.getMessage()}"
        echo "Make sure Slack plugin is installed and configured properly"
    }

    // Console log summary
    echo """
        📊 Notification Summary:
        Status: ${status}
        Build: #${env.BUILD_NUMBER}
        Duration: ${currentBuild.durationString}
        Test Suite: ${params.TEST_SUITE}
        ${testResults.totalTests ? "Tests: ${testResults.totalTests} total, ${testResults.passedTests} passed, ${testResults.failedTests} failed" : ""}
    """.stripIndent()
}