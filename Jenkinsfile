pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven-3.9.11'  // Name configured in Jenkins
        JAVA_HOME = tool 'JDK_21'
        PATH = "${MAVEN_HOME}\\bin;${JAVA_HOME}\\bin;${env.PATH}"
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
                bat "\"${MAVEN_HOME}\\bin\\mvn.cmd\" clean compile -B"
            }
        }
        stage('Unit Tests') {
            steps {
                echo 'Running unit tests...'
                bat "\"${MAVEN_HOME}\\bin\\mvn.cmd\" test -B"
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
