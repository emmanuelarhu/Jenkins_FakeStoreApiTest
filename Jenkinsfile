pipeline {
	agent {
		label 'linux-agent'
	}

    tools {
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
                     "All tests passed. See details: ${env.BUILD_URL}\n" +
                     "See Allure Report Here 👉: ${env.BUILD_URL}allure"
        )
        emailext(
			subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
			body: """✅ Build succeeded!
				<b>Build Details:</b>
				<br>
				<b>Job:</b> ${env.JOB_NAME}
				<b>Build Number:</b> ${env.BUILD_NUMBER}
                <b>Details:</b> ${env.BUILD_URL}
                <b>Allure Report:</b> ${env.BUILD_URL}allure
                """,
                to: "notebooks8.8.8.8@gmail.com"
            )
    }
    failure {
			slackSend(
            color: 'danger',
            message: ":x: *FAILED* - `${env.JOB_NAME} #${env.BUILD_NUMBER}`\n" +
                     "All test failed. See details: ${env.BUILD_URL}\n" +
                     "Please check logs immediately.\n" +
					 "See Allure Report Here 👉: ${env.BUILD_URL}allure"
        )
        emailext(
			subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
			body: """❌ Build failed!
				<b>Build Details:</b>
				<br>
				<b>Job:</b> ${env.JOB_NAME}
				<b>Build Number:</b> ${env.BUILD_NUMBER}
				<b>Details:</b> ${env.BUILD_URL}
				<b>Allure Report:</b> ${env.BUILD_URL}allure
				""",
				to: "notebooks8.8.8.8@gmail.com"
			)
    }
    unstable {
			slackSend(
            color: 'warning',
            message: ":warning: *UNSTABLE* - `${env.JOB_NAME} #${env.BUILD_NUMBER}`\n" +
                     "Some tests failed. See details: `${env.BUILD_URL}`\n" +
                     "See Allure Report Here 👉: ${env.BUILD_URL}allure"
        )
emailext(
    subject: "⚠️ UNSTABLE BUILD: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
    body: """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333;">
            <h2 style="color: orange;">⚠️ Build Unstable!</h2>

            <p>Hello Team,</p>
            <p>The build <b>${env.JOB_NAME} #${env.BUILD_NUMBER}</b> has finished with an <span style="color: orange; font-weight: bold;">UNSTABLE</span> status.</p>

            <h3 style="color: #555;">🔍 Build Details</h3>
            <table border="1" cellpadding="6" cellspacing="0" style="border-collapse: collapse;">
                <tr><th align="left">Job Name</th><td>${env.JOB_NAME}</td></tr>
                <tr><th align="left">Build Number</th><td>${env.BUILD_NUMBER}</td></tr>
                <tr><th align="left">Status</th><td style="color: orange;"><b>UNSTABLE</b></td></tr>
                <tr><th align="left">Triggered By</th><td>${currentBuild.getBuildCauses()[0].shortDescription}</td></tr>
            </table>

            <h3 style="color: #555;">📎 Links</h3>
            <ul>
                <li><a href="${env.BUILD_URL}" style="color: blue;">Jenkins Build Details</a></li>
                <li><a href="${env.BUILD_URL}console" style="color: blue;">Console Output</a></li>
                <li><a href="${env.BUILD_URL}allure" style="color: blue;">Allure Report</a></li>
            </ul>

            <p>Regards,<br><b>Jenkins CI</b></p>
            <p>
        </body>
        </html>
    """,
    mimeType: 'text/html',
    to: "notebooks8.8.8.8@gmail.com"
)

    }
}
	}