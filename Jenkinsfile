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

        stage('Test API') {
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
		success {
			slackSend(
            color: 'good',
            message: ":white_check_mark: *SUCCESS* - `${env.JOB_NAME} #${env.BUILD_NUMBER}`\n" +
                     "See details: ${env.BUILD_URL}\n" +
                     "Allure Report: ${env.BUILD_URL}allure"
        )
    }
    failure {
			slackSend(
            color: 'danger',
            message: ":x: *FAILED* - `${env.JOB_NAME} #${env.BUILD_NUMBER}`\n" +
                     "See details: ${env.BUILD_URL}\n" +
                     "Please check logs immediately."
        )
    }
    unstable {
			slackSend(
            color: 'warning',
            message: ":warning: *UNSTABLE* - `${env.JOB_NAME} #${env.BUILD_NUMBER}`\n" +
                     "Some tests failed. See: `${env.BUILD_URL}`\n" +
                     "See Allure Report Here 👉: `${env.JOB_NAME} #${env.BUILD_NUMBER}`allure"
        )
    }
}
	}