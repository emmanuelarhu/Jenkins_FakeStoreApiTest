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
                // Run tests and generate allure results
                sh 'mvn clean test'
            }
            post {
				always {
					// Archive surefire reports
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true

                    // Publish TestNG results
                    publishTestResults testResultsPattern: 'target/surefire-reports/TEST-*.xml'

                    // Check what was generated
                    script {
						sh '''
                            echo "=== Checking generated files ==="
                            echo "Surefire reports:"
                            ls -la target/surefire-reports/ || echo "No surefire-reports"

                            echo "Allure results:"
                            ls -la target/allure-results/ || echo "No allure-results"

                            if [ -d "target/allure-results" ]; then
                                echo "Allure result files:"
                                find target/allure-results -type f | head -10
                            fi
                        '''
                    }
                }
            }
        }
    }

    post {
		always {
			// This is the key part - Allure plugin reads from target/allure-results
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'target/allure-results']]
            ])

            echo '✅ Pipeline completed'
            echo "📊 View Allure Report: ${BUILD_URL}allure/"
            echo "📈 View Test Results: ${BUILD_URL}testReport/"
        }

        success {
			echo '🎉 All tests passed!'
        }

        failure {
			echo '❌ Some tests failed - check reports'
        }
    }
}