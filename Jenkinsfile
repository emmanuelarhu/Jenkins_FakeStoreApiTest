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
                sh 'mvn clean test'
            }
            post {
				always {
					// Archive surefire reports
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                    publishTestResults testResultsPattern: 'target/surefire-reports/TEST-*.xml'

                    script {
						// Check what was generated
                        sh '''
                            echo "=== Generated Test Results ==="
                            echo "Allure results:"
                            ls -la target/allure-results/ || echo "No allure-results"
                            echo "Number of allure files: $(find target/allure-results -name '*.json' | wc -l)"
                        '''
                    }
                }
            }
        }

        stage('Generate & Serve Allure Report') {
			steps {
				echo '📊 Generating Allure report from target/allure-results...'
                script {
					try {
						// Install Allure in workspace (no permission issues)
                        sh '''
                            # Check if allure is already available in workspace
                            if [ ! -f "./allure-2.24.0/bin/allure" ]; then
                                echo "📦 Installing Allure to workspace..."
                                curl -o allure-2.24.0.tgz -Ls https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/2.24.0/allure-commandline-2.24.0.tgz
                                tar -zxf allure-2.24.0.tgz
                                chmod +x allure-2.24.0/bin/allure
                            fi

                            # Verify allure works
                            ./allure-2.24.0/bin/allure --version
                        '''

                        // Generate static report from allure-results
                        sh '''
                            echo "🚀 Generating Allure report from target/allure-results..."

                            # Generate static HTML report
                            mkdir -p target/allure-report
                            ./allure-2.24.0/bin/allure generate target/allure-results --output target/allure-report --clean

                            echo "✅ Allure report generated!"
                            echo "📁 Report contents:"
                            ls -la target/allure-report/

                            # Verify index.html exists
                            if [ -f "target/allure-report/index.html" ]; then
                                echo "✅ index.html found - report ready!"
                            else
                                echo "❌ index.html not found!"
                                exit 1
                            fi
                        '''

                    } catch (Exception e) {
						echo "⚠️ Allure report generation failed: ${e.getMessage()}"

                        // Create fallback if generation fails
                        sh '''
                            mkdir -p target/allure-report
                            echo '<!DOCTYPE html>
<html>
<head><title>Allure Report Generation Failed</title></head>
<body>
    <h1>⚠️ Allure Report Generation Failed</h1>
    <p>Check console output for details.</p>
    <p><a href="../testReport/">View TestNG Results Instead</a></p>
</body>
</html>' > target/allure-report/index.html
                        '''
                    }
                }
            }
        }

        stage('Publish Allure Report') {
			steps {
				echo '📊 Publishing Allure report...'

                // Archive the allure results
                archiveArtifacts artifacts: 'target/allure-results/**/*', allowEmptyArchive: true

                // Archive the generated report
                archiveArtifacts artifacts: 'target/allure-report/**/*', allowEmptyArchive: true

                // Publish the HTML report so Jenkins serves it
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/allure-report',
                    reportFiles: 'index.html',
                    reportName: 'Allure Report',
                    reportTitles: 'FakeStore API Test Results'
                ])

                echo "📊 Allure Report published!"
                echo "🔗 Access report at: ${BUILD_URL}Allure_20Report/"
            }
        }
    }

    post {
		always {
			script {
				def allureFileCount = sh(
                    script: 'find target/allure-results -name "*.json" 2>/dev/null | wc -l || echo "0"',
                    returnStdout: true
                ).trim()

                echo "🏁 Pipeline completed!"
                echo "📊 Generated ${allureFileCount} Allure result files"
                echo "🔗 View Allure Report: ${BUILD_URL}Allure_20Report/"
                echo "📈 View TestNG Results: ${BUILD_URL}testReport/"
            }
        }

        success {
			echo '🎉 All tests completed successfully! '
            echo "✅ Allure report is served from target/allure-results"
        }

        failure {
			echo '❌ Pipeline had issues - check console output'
        }
    }
}