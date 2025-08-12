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
					archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                }
            }
        }

        stage('Generate Allure Report') {
			steps {
				echo '📊 Generating Allure HTML report...'
                // Generate HTML from allure-results
                sh '''
                    mkdir -p target/allure-report
                    if [ -d "target/allure-results" ]; then
                        allure generate target/allure-results -o target/allure-report --clean
                    else
                        echo "No allure-results found, skipping HTML generation."
                    fi
                '''
            }
        }

        stage('Publish Reports') {
			steps {
				echo '📊 Publishing Allure HTML report to Jenkins...'

                // HTML Publisher
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/allure-report', // ✅ HTML directory, not allure-results
                    reportFiles: 'index.html',
                    reportName: 'Allure Report'
                ])

                // Allure Plugin (optional)
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
                    }
                }
            }
        }
    }

    post {
		always {
			echo '🧹 Pipeline completed'
            archiveArtifacts artifacts: 'target/allure-results/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/allure-report/**/*', allowEmptyArchive: true
        }
    }
}