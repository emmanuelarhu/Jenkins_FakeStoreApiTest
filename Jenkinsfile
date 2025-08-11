pipeline {
	agent any

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
                sh 'mvn test'
            }
            post {
				always {
					// Archive test results
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true

                    // Publish TestNG results
                    //publishTestResults testResultsPattern: 'target/surefire-reports/TEST-*.xml'
                }
            }
        }

        stage('Generate Allure Report') {
			steps {
				echo '📊 Generating Allure reports...'
                script {
					try {
						// First, ensure allure-results directory exists and has content
                        sh '''
                            echo "Checking allure-results directory..."
                            ls -la target/allure-results/ || echo "No allure-results directory found"
                            if [ -d "target/allure-results" ]; then
                                echo "Allure results files:"
                                find target/allure-results -type f -name "*.json" | head -10
                            fi
                        '''

                        // Generate Allure report using the Allure tool (not Maven plugin)
                        sh '''
                            # Install Allure if not present
                            if ! command -v allure &> /dev/null; then
                                echo "Installing Allure..."
                                curl -o allure-2.24.0.tgz -Ls https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/2.24.0/allure-commandline-2.24.0.tgz
                                tar -zxvf allure-2.24.0.tgz -C /opt/
                                ln -s /opt/allure-2.24.0/bin/allure /usr/bin/allure
                            fi

                            # Generate the report
                            mkdir -p target/allure-report
                            allure generate target/allure-results --output target/allure-report --clean
                        '''

                        echo "✅ Allure report generated successfully!"

                        // Verify report was generated
                        sh '''
                            echo "Generated report contents:"
                            ls -la target/allure-report/
                            echo "Checking for index.html:"
                            ls -la target/allure-report/index.html || echo "index.html not found"
                        '''

                    } catch (Exception e) {
						echo "⚠️ Allure report generation failed: ${e.getMessage()}"

                        // Create a fallback HTML report
                        sh '''
                            mkdir -p target/allure-report
                            cat > target/allure-report/index.html << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <title>FakeStore API Test Results</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .header { background: #f4f4f4; padding: 20px; border-radius: 5px; }
        .content { margin: 20px 0; }
        .status { padding: 10px; border-radius: 3px; margin: 10px 0; }
        .error { background: #ffe6e6; border-left: 4px solid #ff0000; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🧪 FakeStore API Test Results</h1>
        <p><strong>Build:</strong> ${BUILD_NUMBER}</p>
        <p><strong>Date:</strong> $(date)</p>
    </div>

    <div class="content">
        <div class="status error">
            <h3>⚠️ Allure Report Generation Failed</h3>
            <p>The Allure report could not be generated, but tests were executed successfully.</p>
        </div>

        <h3>📊 Available Reports:</h3>
        <ul>
            <li><a href="../../testReport/" target="_blank">TestNG/Surefire Results</a></li>
            <li><a href="../../artifact/" target="_blank">Build Artifacts</a></li>
        </ul>
    </div>
</body>
</html>
EOF
                        '''
                    }
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

                // Try to use Allure Jenkins plugin if available
                script {
					try {
						allure([
                            includeProperties: false,
                            jdk: '',
                            properties: [],
                            reportBuildPolicy: 'ALWAYS',
                            results: [[path: 'target/allure-results']]
                        ])
                        echo "✅ Allure Jenkins plugin report published!"
                    } catch (Exception e) {
						echo "⚠️ Allure Jenkins plugin not available: ${e.getMessage()}"
                        echo "💡 Install Allure Jenkins plugin for better integration"
                    }
                }

                echo "📊 Report URLs:"
                echo "  🔗 HTML Report: ${BUILD_URL}Allure_20Report/"
                echo "  🔗 Test Results: ${BUILD_URL}testReport/"
                echo "  🔗 Artifacts: ${BUILD_URL}artifact/target/allure-report/"
            }
        }
    }

    post {
		always {
			echo '🧹 Pipeline completed'
            // Archive allure results for debugging
            archiveArtifacts artifacts: 'target/allure-results/**/*', allowEmptyArchive: true
        }

        success {
			echo '✅ Pipeline completed successfully!'
            echo "📊 View reports at: ${BUILD_URL}Allure_20Report/"
        }

        failure {
			echo '❌ Pipeline failed!'
            echo "📊 Check results at: ${BUILD_URL}testReport/"
        }
    }
}