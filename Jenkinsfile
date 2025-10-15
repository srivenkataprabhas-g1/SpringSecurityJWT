pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven-3.9.11'  // ensure this matches Jenkins global config
        JAVA_HOME = tool 'JDK_21'        // ensure this matches Jenkins global config
        PATH = "${MAVEN_HOME}\\bin;${JAVA_HOME}\\bin;${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                echo 'Compiling source code...'
                bat "\"${MAVEN_HOME}\\bin\\mvn.cmd\" clean compile -B"
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application...'
                bat "\"${MAVEN_HOME}\\bin\\mvn.cmd\" package -DskipTests -B"
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
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
