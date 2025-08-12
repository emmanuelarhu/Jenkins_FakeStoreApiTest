pipeline {
	agent any

    stages {
		stage('Checkout') {
			steps {
				echo '📁 Checking out code... '
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
                }
            }
        }

        stage('Allure Report') {
			steps {
				script {
					echo "📊 Publishing Allure report..."
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

    //post {
	//	always {
	//		echo '🧹 Pipeline completed'
    //        // Archive allure results for debugging
    //        archiveArtifacts artifacts: 'target/allure-results/**/*', allowEmptyArchive: true
    //    }
	//
    //    success {
	//		echo '✅ Pipeline completed successfully!'
    //        echo "📊 View reports at: ${env.BUILD_URL}Allure_20Report/"
    //    }
	//
    //    failure {
	//		echo '❌ Pipeline failed!'
    //        echo "📊 Check results at: ${env.BUILD_URL}testReport/"
    //    }
    //}
	}
	}

	// Optional: Post actions for the pipeline
	post {
		always {
			echo '🧹 Cleaning up...'
			cleanWs()
		}
		success {
			echo '✅ Pipeline completed successfully!'
			echo "📊 View reports at: ${env.BUILD_URL}Allure_20Report/"
		}
		failure {
			echo '❌ Pipeline failed!'
			echo "📊 Check results at: ${env.BUILD_URL}testReport/"
		}
	}