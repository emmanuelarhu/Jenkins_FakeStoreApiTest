pipeline {
	agent any

    tools {
		maven 'Maven 3.8.5'  // if using Maven
        // or skip if you use Gradle
    }

    stages {
		stage('Checkout') {
			steps {
				checkout scm
            }
        }

        stage('Run Tests') {
			steps {
				sh 'mvn clean test' // your REST Assured tests with Allure listener
            }
        }

        stage('Allure Report') {
			steps {
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
