pipeline {
	agent any

    tools {
		// This tells Jenkins to use the Allure tool we configured
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
                    publishTestResults testResultsPattern: 'target/surefire-reports/TEST-*.xml'
                }
            }
        }
    }

    post {
		always {
			allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'target/allure-results']]
            ])

            echo "📊 View Allure Report: ${BUILD_URL}allure/"
        }
    }
}