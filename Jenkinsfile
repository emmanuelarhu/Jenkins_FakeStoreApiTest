pipeline {
	agent any

    tools {
		// This must match the name you set in Global Tool Configuration
        allure 'Allure'
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
                sh 'mvn clean test'
            }
            post {
				always {
					archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true

                    script {
						sh '''
                            echo "=== Allure Results Check ==="
                            ls -la target/allure-results/ || echo "No allure-results directory"
                            echo "Number of allure files: $(find target/allure-results -name '*.json' | wc -l)"
                            echo "Allure tool available: $(which allure || echo 'Not in PATH')"
                        '''
                    }
                }
            }
        }

        stage('Allure Report') {
			steps {
				echo '📊 Generating Allure Report...'
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                ])
            }
        }
    }

    post {
		always {
			echo '✅ Pipeline completed'
            echo "📊 View Allure Report: ${BUILD_URL}allure/"
            echo "📈 View Test Results: ${BUILD_URL}testReport/"
        }

        success {
			echo '🎉 All tests passed!'
        }

        failure {
			echo '❌ Pipeline failed - check reports'
        }
    }
}