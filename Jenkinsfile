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
                    publishTestResults testResultsPattern: 'target/surefire-reports/TEST-*.xml'
                }
            }
        }

        stage('Generate Allure Report') {
			steps {
				echo '📊 Publishing Allure Report...'
                script {
					// Check if allure-results exist
                    sh '''
                        echo "Checking for allure-results..."
                        ls -la target/allure-results/ || echo "No allure-results found"
                    '''
                }
            }
            post {
				always {
					// Let Jenkins Allure plugin handle everything
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
    }

    post {
		always {
			echo '✅ Pipeline completed'
            echo "📊 View Allure Report: ${BUILD_URL}allure/"
            echo "📈 View Test Results: ${BUILD_URL}testReport/"
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