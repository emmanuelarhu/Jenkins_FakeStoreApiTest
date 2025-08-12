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
					// Archive test results
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true

                    // Publish TestNG results
                    //publishTestResults testResultsPattern: 'target/surefire-reports/TEST-*.xml'

                    // Check what was generated
                    script {
						sh '''
                            echo "=== What got generated ==="
                            echo "Allure results:"
                            ls -la target/allure-results/ || echo "No allure-results"

                            echo "Number of allure result files:"
                            find target/allure-results -name "*.json" 2>/dev/null | wc -l || echo "0"
                        '''
                    }
                }
            }
        }

        stage('Allure reports') {
			steps {
				script {
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
			// ONLY use the Jenkins Allure plugin - no manual installation
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
			echo '🎉 Tests passed! Check the Allure report'
        }

        failure {
			echo '❌ Tests failed - check reports for details'
        }
    }
}