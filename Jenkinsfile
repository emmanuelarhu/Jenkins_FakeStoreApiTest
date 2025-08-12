pipeline {
	agent any

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
