pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/srivenkataprabhas-g1/SpringSecurityJWT.git'
            }
        }

        stage('Build') {
            steps {
                dir('SpringSecurityJWTDemo-main') { // Use only if project is in subfolder
                    sh 'mvn clean package'
                }
            }
        }

        stage('Test') {
            steps {
                dir('SpringSecurityJWTDemo-main') {
                    sh 'mvn test'
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying to test environment...'
                // You can add a simple script to run the JAR, or copy files, etc.
            }
        }
    }
}
