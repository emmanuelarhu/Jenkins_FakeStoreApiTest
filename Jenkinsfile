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
                sh '/usr/share/maven/bin/mvn test'
            }
            post {
				always {
					// Archive test results
                archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true

                // Publish test results
                //junit testResultsPattern: 'target/surefire-reports/TEST-*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Generate Reports') {
			steps {
				echo '📊 Generating Allure reports...'
                script {
					try {
						// Generate Allure report (creates static HTML files)
                   sh 'mvn allure:report'

                   echo "✅ Allure report generated successfully!"
                   echo "📁 Report location: target/site/allure-maven-plugin/"

                   // List generated files for debugging
                   sh 'ls -la target/site/allure-maven-plugin/ || echo "No allure report directory found"'

                } catch (Exception e) {
						echo "⚠️ Allure report generation failed: ${e.getMessage()}"

                   // Create a basic HTML report as fallback
                   sh '''
                       mkdir -p target/site/allure-maven-plugin
                       cat > target/site/allure-maven-plugin/index.html << 'EOF'
<!DOCTYPE html>
<html>
<head><title>FakeStore API Test Results</title></head>
<body>
    <h1>🧪 FakeStore API Test Results</h1>
    <p>Build: ${BUILD_NUMBER}</p>
    <p>Date: $(date)</p>
    <p>Allure report generation failed, but tests were executed.</p>
    <p><a href="../../../testReport/">View TestNG Results</a></p>
</body>
</html>
EOF
                   '''
                }
            }
          }
        }

        stage('Publish Reports') {
			steps {
				echo '📊 Publishing reports to Jenkins...'

             // Method 1: Publish HTML reports in Jenkins (Recommended)
             publishHTML([
                 allowMissing: false,
                 alwaysLinkToLastBuild: true,
                 keepAll: true,
                 reportDir: 'target/site/allure-maven-plugin',
                 reportFiles: 'index.html',
                 reportName: 'Allure Report',
                 reportTitles: 'FakeStore API Test Results'
             ])

             // Method 2: Archive all report files for download
             archiveArtifacts artifacts: 'target/site/allure-maven-plugin/**/*', allowEmptyArchive: true

             // Method 3: If you have Allure Jenkins plugin installed
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

             echo "📊 Report URLs:"
             echo "  🔗 HTML Report: ${BUILD_URL}Allure_Report/"
             echo "  🔗 Test Results: ${BUILD_URL}testReport/"
             echo "  🔗 Build Artifacts: ${BUILD_URL}artifact/"
          }
        }
    }

    post {
		always {
			echo '🧹 Cleaning up workspace...'
          // Don't clean workspace immediately so reports remain accessible
          // cleanWs()
        }

        success {
			echo '✅ Pipeline completed successfully!'
          echo "📊 View reports at: ${BUILD_URL}Allure_Report/"
        }

        failure {
			echo '❌ Pipeline failed!'
          echo "📊 Check results at: ${BUILD_URL}testReport/"
        }
    }
}