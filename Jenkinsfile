pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven-3.8.6'
        JAVA_HOME = tool 'JDK-21'
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${PATH}"
        APP_NAME = 'spring-security-jwt-demo'
        APP_VERSION = "${BUILD_NUMBER}"
        TEST_PORT = '8080'
        TEST_PROFILE = 'test'
    }

    parameters {
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip running tests')
        booleanParam(name: 'DEPLOY_TO_TEST', defaultValue: true, description: 'Deploy to test environment after build')
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
                sh 'mvn clean compile -B'
            }
        }

        stage('Unit Tests') {
            when {
                expression { !params.SKIP_TESTS }
            }
            steps {
                echo 'Running unit tests...'
                sh 'mvn test -B'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Code Quality') {
            parallel {
                stage('Coverage') {
                    steps {
                        sh 'mvn jacoco:report'
                    }
                }
                stage('Static Analysis') {
                    steps {
                        sh 'mvn spotbugs:check || true'
                        sh 'mvn checkstyle:check || true'
                    }
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging application...'
                sh 'mvn package -DskipTests=true -B'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy to Test') {
            when {
                expression { params.DEPLOY_TO_TEST }
            }
            steps {
                echo 'Simulating deploy to test environment...'
                // Add your deployment commands here for non-Docker environment
                // For example, use SSH to remote server or start app manually
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
