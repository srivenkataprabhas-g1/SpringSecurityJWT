pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven-3.8.6'  // Ensure this tool is configured
        JAVA_HOME = tool 'JDK-17'        // Ensure this tool is configured
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building Spring Boot application...'
                bat 'mvn clean compile -B'
            }
        }

        stage('Unit Tests') {
            steps {
                echo 'Running unit tests...'
                bat 'mvn test -B'
            }
            post {
                always {
                    junit 'target\\surefire-reports\\*.xml'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Build and tests completed successfully!'
        }
        failure {
            echo 'Build or tests failed!'
        }
    }
}
