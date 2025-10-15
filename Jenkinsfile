pipeline {
    agent any

    tools {
        jdk 'jdk21'         // Replace with your actual JDK name in Jenkins tool config
        gradle 'gradle7'    // Replace with your actual Gradle tool name
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/srivenkataprabhas-g1/SpringSecurityJWT.git'
            }
        }

        stage('Build') {
            steps {
                dir('SpringSecurityJWTDemo-main') {
                    sh './gradlew clean build'
                }
            }
        }

        stage('Test') {
            steps {
                dir('SpringSecurityJWTDemo-main') {
                    sh './gradlew test'
                }
            }
            post {
                always {
                    junit 'SpringSecurityJWTDemo-main/build/test-results/test/*.xml'
                }
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'SpringSecurityJWTDemo-main/build/libs/*.jar', fingerprint: true
            }
        }

        stage('Deploy (optional)') {
            steps {
                echo 'You can deploy the app here (e.g., SSH to server, Docker run, etc.)'
            }
        }
    }

    post {
        success {
            echo 'Build completed successfully.'
        }
        failure {
            echo 'Build failed.'
        }
    }
}
