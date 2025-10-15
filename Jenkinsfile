pipeline {
    agent any

    tools {
        jdk 'JDK _21'       // Match the name exactly as you have it
        maven 'Maven'       // Matches your configured Maven
    }

    environment {
        MAVEN_OPTS = "-Dmaven.test.failure.ignore=false"
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'c483ec91-2f63-4c94-908a-bd485275d21b', url: 'https://github.com/srivenkataprabhas-g1/SpringSecurityJWT.git'
            }
        }

        stage('Build with Maven') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo '✅ Build completed successfully.'
        }
        failure {
            echo '❌ Build failed. Check logs for more details.'
        }
    }
}
