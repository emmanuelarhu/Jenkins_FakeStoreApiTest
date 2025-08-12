pipeline {
	agent any

    environment {
		API_BASE_URL = "https://fakestoreapi.com"
        ALLURE_HOME = "/var/jenkins_home/allure"
    }

    stages {
		stage('Checkout') {
			steps {
				echo '📁 Checking out code...'
                checkout scm
            }
        }

        stage('Build') {
			steps {
				echo '🔨 Building project...'
                sh 'mvn clean compile test-compile'
            }
        }

        stage('Test') {
			steps {
				echo '🧪 Running API tests...'
                sh "mvn test -DAPI_BASE_URL=${env.API_BASE_URL}"
            }
            post {
				always {
					// Archive test results
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true

                    // Publish TestNG results (FIXED: Uncommented)
                    script {
						try {
							publishTestResults testResultsPattern: 'target/surefire-reports/TEST-*.xml'
                            echo "✅ TestNG results published"
                        } catch (Exception e) {
							echo "⚠️ TestNG results publishing failed: ${e.getMessage()}"
                        }
                    }
                }
            }
        }

        stage('Install Allure') {
			steps {
				echo '⚙️ Setting up Allure...'
                script {
					try {
						sh '''
                            # Check if allure is already installed and working
                            if command -v allure &> /dev/null && allure --version; then
                                echo "✅ Allure already installed and working"
                                allure --version
                            else
                                echo "📦 Installing Allure to Jenkins home directory..."

                                # Create allure directory in Jenkins home (with permissions)
                                mkdir -p ${ALLURE_HOME}
                                cd ${ALLURE_HOME}

                                # Download and extract Allure
                                if [ ! -f "allure-2.24.0.tgz" ]; then
                                    curl -o allure-2.24.0.tgz -Ls https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/2.24.0/allure-commandline-2.24.0.tgz
                                fi

                                # Extract if not already extracted
                                if [ ! -d "allure-2.24.0" ]; then
                                    tar -zxf allure-2.24.0.tgz
                                fi

                                # Make allure executable
                                chmod +x ${ALLURE_HOME}/allure-2.24.0/bin/allure

                                # Verify installation
                                ${ALLURE_HOME}/allure-2.24.0/bin/allure --version
                                echo "✅ Allure installed successfully to ${ALLURE_HOME}"
                            fi
                        '''
                    } catch (Exception e) {
						echo "⚠️ Allure installation failed: ${e.getMessage()}"
                        echo "📝 Will use Maven plugin as fallback"
                    }
                }
            }
        }

        stage('Generate Allure Report') {
			steps {
				echo '📊 Generating Allure reports...'
                script {
					// Check if allure-results exist
                    sh '''
                        echo "🔍 Checking allure-results directory..."
                        if [ -d "target/allure-results" ]; then
                            echo "✅ Found allure-results directory:"
                            ls -la target/allure-results/
                            echo "📊 Number of result files:"
                            find target/allure-results -name "*.json" | wc -l
                        else
                            echo "❌ No allure-results directory found"
                            echo "📁 Available target contents:"
                            ls -la target/ || echo "No target directory"
                        fi
                    '''

                    def reportGenerated = false

                    // Try Method 1: Allure command line
                    try {
						echo "🚀 Method 1: Using Allure command line..."
                        sh '''
                            # Use installed allure
                            ALLURE_CMD=""
                            if command -v allure &> /dev/null; then
                                ALLURE_CMD="allure"
                            elif [ -f "${ALLURE_HOME}/allure-2.24.0/bin/allure" ]; then
                                ALLURE_CMD="${ALLURE_HOME}/allure-2.24.0/bin/allure"
                            else
                                echo "❌ No allure command found"
                                exit 1
                            fi

                            echo "📊 Using allure command: $ALLURE_CMD"

                            # Generate report
                            mkdir -p target/allure-report
                            $ALLURE_CMD generate target/allure-results --output target/allure-report --clean

                            echo "✅ Allure report generated successfully!"
                            ls -la target/allure-report/
                        '''
                        reportGenerated = true
                        echo "✅ Method 1 successful: Allure command line report generated"

                    } catch (Exception e1) {
						echo "⚠️ Method 1 failed: ${e1.getMessage()}"

                        // Try Method 2: Maven Allure plugin
                        try {
							echo "🚀 Method 2: Using Maven Allure plugin..."
                            sh '''
                                echo "📊 Generating report with Maven plugin..."
                                mvn allure:report -q

                                # Check if report was generated
                                if [ -d "target/site/allure-maven-plugin" ]; then
                                    echo "✅ Maven plugin generated report"

                                    # Copy to expected location
                                    mkdir -p target/allure-report
                                    cp -r target/site/allure-maven-plugin/* target/allure-report/ 2>/dev/null || true

                                    ls -la target/allure-report/
                                else
                                    echo "❌ Maven plugin did not generate report"
                                    exit 1
                                fi
                            '''
                            reportGenerated = true
                            echo "✅ Method 2 successful: Maven plugin report generated"

                        } catch (Exception e2) {
							echo "⚠️ Method 2 failed: ${e2.getMessage()}"
                            echo "📝 Both methods failed, creating fallback report..."
                        }
                    }

                    // Create fallback report if both methods failed
                    if (!reportGenerated) {
						echo "📄 Creating enhanced fallback HTML report..."
                        sh """
                            mkdir -p target/allure-report
                            cat > target/allure-report/index.html << 'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FakeStore API Test Results</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: white;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }
        .header h1 { font-size: 2.5em; margin-bottom: 10px; }
        .header p { font-size: 1.2em; opacity: 0.9; }
        .content { padding: 40px; }
        .info-section {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin: 30px 0;
        }
        .info-card {
            background: #f8f9fa;
            padding: 25px;
            border-radius: 10px;
            border-left: 5px solid #667eea;
            transition: transform 0.3s ease;
        }
        .info-card:hover { transform: translateY(-5px); }
        .info-card h3 { color: #333; margin-bottom: 15px; font-size: 1.3em; }
        .info-card p { color: #666; line-height: 1.6; }
        .status-alert {
            background: linear-gradient(135deg, #ffeaa7 0%, #fdcb6e 100%);
            padding: 25px;
            border-radius: 10px;
            margin: 25px 0;
            border-left: 5px solid #e17055;
            box-shadow: 0 4px 15px rgba(253, 203, 110, 0.3);
        }
        .status-alert h3 { color: #2d3436; margin-bottom: 15px; }
        .status-alert p { color: #2d3436; line-height: 1.6; }
        .links-section { margin-top: 40px; }
        .links-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }
        .link-card {
            background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
            color: white;
            padding: 25px;
            border-radius: 10px;
            text-decoration: none;
            text-align: center;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(116, 185, 255, 0.3);
        }
        .link-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(116, 185, 255, 0.4);
        }
        .link-card h4 { margin-bottom: 10px; font-size: 1.2em; }
        .footer {
            background: #2d3436;
            color: white;
            padding: 25px;
            text-align: center;
        }
        .timestamp { font-family: 'Courier New', monospace; background: #ddd; padding: 5px 10px; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🧪 FakeStore API Test Results</h1>
            <p>Automated Test Execution Report</p>
        </div>

        <div class="content">
            <div class="info-section">
                <div class="info-card">
                    <h3>🏗️ Build Information</h3>
                    <p><strong>Build Number:</strong> ${BUILD_NUMBER}</p>
                    <p><strong>Job Name:</strong> ${JOB_NAME}</p>
                    <p><strong>Jenkins URL:</strong> ${JENKINS_URL}</p>
                </div>

                <div class="info-card">
                    <h3>🎯 Test Configuration</h3>
                    <p><strong>API Target:</strong> ${API_BASE_URL}</p>
                    <p><strong>Test Framework:</strong> TestNG + RestAssured</p>
                    <p><strong>Build Tool:</strong> Maven</p>
                </div>

                <div class="info-card">
                    <h3>⏰ Execution Details</h3>
                    <p><strong>Date:</strong> <span class="timestamp">\$(date)</span></p>
                    <p><strong>Status:</strong> Tests Completed</p>
                    <p><strong>Environment:</strong> Jenkins Pipeline</p>
                </div>
            </div>

            <div class="status-alert">
                <h3>⚠️ Allure Report Generation Status</h3>
                <p><strong>Issue:</strong> Both Allure command-line and Maven plugin failed to generate the full report.</p>
                <p><strong>Cause:</strong> This could be due to missing dependencies, permission issues, or Allure installation problems.</p>
                <p><strong>Impact:</strong> All tests were executed successfully. This is only a reporting visualization issue.</p>
            </div>

            <div class="links-section">
                <h3>📊 Available Test Reports & Resources</h3>
                <div class="links-grid">
                    <a href="../../testReport/" class="link-card" target="_blank">
                        <h4>📈 TestNG Results</h4>
                        <p>Detailed test execution results with pass/fail status</p>
                    </a>

                    <a href="../../artifact/" class="link-card" target="_blank">
                        <h4>📦 Build Artifacts</h4>
                        <p>Download test reports, logs, and generated files</p>
                    </a>

                    <a href="../../console" class="link-card" target="_blank">
                        <h4>🖥️ Console Output</h4>
                        <p>View complete build execution logs</p>
                    </a>

                    <a href="../../" class="link-card" target="_blank">
                        <h4>🏠 Build Details</h4>
                        <p>Return to main build information page</p>
                    </a>
                </div>
            </div>
        </div>

        <div class="footer">
            <p>Generated by Jenkins Pipeline • FakeStore API Testing Suite</p>
            <p>For technical support, check the console output and build logs</p>
        </div>
    </div>
</body>
</html>
EOF
                        """
                    }

                    // Verify final report exists
                    sh '''
                        echo "🔍 Final verification:"
                        if [ -f "target/allure-report/index.html" ]; then
                            echo "✅ Report index.html exists"
                            ls -la target/allure-report/index.html
                        else
                            echo "❌ No index.html found!"
                        fi
                    '''
                }
            }
        }

        stage('Publish Reports') {
			steps {
				echo '📊 Publishing reports to Jenkins...'

                // Publish HTML reports
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/allure-report',
                    reportFiles: 'index.html',
                    reportName: 'Allure Report',
                    reportTitles: 'FakeStore API Test Results'
                ])

                // Archive report files
                archiveArtifacts artifacts: 'target/allure-report/**/*', allowEmptyArchive: true

                // Try Jenkins Allure plugin with proper configuration
                script {
					try {
						// Configure Allure installation for Jenkins plugin
                        sh '''
                            echo "🔧 Configuring Allure for Jenkins plugin..."
                            if [ -d "${ALLURE_HOME}/allure-2.24.0" ]; then
                                echo "ALLURE_HOME=${ALLURE_HOME}/allure-2.24.0" > allure.properties
                                cat allure.properties
                            fi
                        '''

                        allure([
                            includeProperties: false,
                            jdk: '',
                            properties: [],
                            reportBuildPolicy: 'ALWAYS',
                            results: [[path: 'target/allure-results']],
                            commandline: "${env.ALLURE_HOME}/allure-2.24.0"
                        ])
                        echo "✅ Allure Jenkins plugin report published!"

                    } catch (Exception e) {
						echo "⚠️ Allure Jenkins plugin failed: ${e.getMessage()}"
                        echo "💡 This is normal if plugin configuration needs adjustment"
                    }
                }

                // Display all available report URLs
                script {
					echo "📊 Report Access Information:"
                    echo "  🔗 Main Report: ${BUILD_URL}Allure_20Report/"
                    echo "  📈 TestNG Results: ${BUILD_URL}testReport/"
                    echo "  📦 Artifacts: ${BUILD_URL}artifact/target/allure-report/"
                    echo "  🖥️ Console Output: ${BUILD_URL}console"
                    echo "  🏠 Build Home: ${BUILD_URL}"
                }
            }
        }
    }

    post {
		always {
			echo '🧹 Pipeline cleanup and final reporting...'

            // Archive everything for debugging
            archiveArtifacts artifacts: 'target/allure-results/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true

            script {
				// Final status report
                sh '''
                    echo ""
                    echo "📋 Final Build Summary:"
                    echo "  📁 Workspace: $(pwd)"
                    echo "  🎯 API Target: ${API_BASE_URL}"
                    echo "  📊 Results Generated: $(date)"

                    echo ""
                    echo "📂 Generated Artifacts:"
                    [ -d "target/allure-report" ] && echo "  ✅ Allure Report: $(ls target/allure-report | wc -l) files" || echo "  ❌ No Allure Report"
                    [ -d "target/surefire-reports" ] && echo "  ✅ Surefire Reports: $(ls target/surefire-reports | wc -l) files" || echo "  ❌ No Surefire Reports"
                    [ -d "target/allure-results" ] && echo "  ✅ Allure Results: $(ls target/allure-results | wc -l) files" || echo "  ❌ No Allure Results"
                '''
            }
        }

        success {
			echo '🎉 Pipeline completed successfully!'
            echo "📊 Access your reports:"
            echo "  • Main Dashboard: ${BUILD_URL}Allure_20Report/"
            echo "  • Test Details: ${BUILD_URL}testReport/"
        }

        failure {
			echo '❌ Pipeline failed!'
            echo "🔍 Troubleshooting resources:"
            echo "  • Console Logs: ${BUILD_URL}console"
            echo "  • Build Artifacts: ${BUILD_URL}artifact/"
        }
    }
}