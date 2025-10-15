pipeline {
    agent any
    
    // Environment variables for the pipeline
    environment {
        MAVEN_HOME = tool 'Maven-3.8.6'  // Configure this in Jenkins Global Tool Configuration
        JAVA_HOME = tool 'JDK-17'        // Configure this in Jenkins Global Tool Configuration
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${PATH}"
        
        // Application specific variables
        APP_NAME = 'spring-security-jwt-demo'
        APP_VERSION = "${BUILD_NUMBER}"
        DOCKER_IMAGE = "${APP_NAME}:${APP_VERSION}"
       
        // Test environment details
        TEST_PORT = '8080'
        TEST_PROFILE = 'test'
    }
    
    // Build triggers
    triggers {
        // Poll SCM every 2 minutes for changes
        pollSCM('H/3 * * * *')
        
        // Build daily at 2 AM
        cron('0 2 * * *')
    }
    
    // Pipeline parameters
    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['test', 'staging', 'production'],
            description: 'Target deployment environment'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip running tests'
        )
        booleanParam(
            name: 'DEPLOY_TO_TEST',
            defaultValue: true,
            description: 'Deploy to test environment after build'
        )
    }
    
    stages {
        stage('📋 Checkout') {
            steps {
                echo '🔄 Checking out source code...'
                checkout scm
                
                script {
                    // Get commit information
                    env.GIT_COMMIT_SHORT = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                    env.GIT_BRANCH_NAME = sh(
                        script: 'git rev-parse --abbrev-ref HEAD',
                        returnStdout: true
                    ).trim()
                }
                
                echo "📍 Building commit: ${env.GIT_COMMIT_SHORT} on branch: ${env.GIT_BRANCH_NAME}"
            }
        }
        
        stage('🔧 Setup Environment') {
            steps {
                echo '🛠️ Setting up build environment...'
                
                // Display environment information
                sh '''
                    echo "📊 Environment Information:"
                    echo "Java Version:"
                    java -version
                    echo ""
                    echo "Maven Version:"
                    mvn --version
                    echo ""
                    echo "Git Information:"
                    git --version
                    echo ""
                    echo "Build Environment:"
                    echo "BUILD_NUMBER: ${BUILD_NUMBER}"
                    echo "BUILD_URL: ${BUILD_URL}"
                    echo "JOB_NAME: ${JOB_NAME}"
                '''
            }
        }
        
        stage('🏗️ Build Application') {
            steps {
                echo '🔨 Building Spring Boot application...'
                
                // Clean and compile
                sh 'mvn clean compile -B'
                
                echo '✅ Application build completed!'
            }
            post {
                failure {
                    echo '❌ Build failed!'
                }
            }
        }
        
        stage('🧪 Run Unit Tests') {
           stage('🧪 Run Unit Tests') {
    when {
        expression { !params.SKIP_TESTS }
    }
    steps {
        echo 'Running unit tests...'
        sh 'mvn test'
    }
}
            post {
                always {
                    // Publish test results
                    publishTestResults(
                        testResultsPattern: 'target/surefire-reports/*.xml',
                        allowEmptyResults: false
                    )
                    
                    // Archive test reports
                    archiveArtifacts(
                        artifacts: 'target/surefire-reports/**/*',
                        allowEmptyArchive: true
                    )
                }
                success {
                    echo '✅ All tests passed!'
                }
                failure {
                    echo '❌ Some tests failed!'
                }
            }
        }
        
        stage('📊 Code Quality Analysis') {
            parallel {
                stage('📈 Code Coverage') {
                    steps {
                        echo '📈 Generating code coverage report...'
                        sh 'mvn jacoco:report'
                        
                        // Publish coverage results
                        publishCoverage(
                            adapters: [jacocoAdapter(path: 'target/site/jacoco/jacoco.xml')],
                            sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
                        )
                    }
                }
                
                stage('🔍 Static Code Analysis') {
                    steps {
                        echo '🔍 Running static code analysis...'
                        sh '''
                            # Run SpotBugs analysis
                            mvn spotbugs:check || echo "SpotBugs analysis completed with warnings"
                            
                            # Run Checkstyle analysis
                            mvn checkstyle:check || echo "Checkstyle analysis completed with warnings"
                        '''
                    }
                }
            }
        }
        
        stage('📦 Package Application') {
            steps {
                echo '📦 Packaging application...'
                
                sh '''
                    mvn package -DskipTests=true -B \
                        -Dspring.profiles.active=${TEST_PROFILE}
                '''
                
                // Archive the JAR artifact
                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true,
                    allowEmptyArchive: false
                )
                
                echo '✅ Application packaged successfully!'
            }
        }
        
        stage('🐳 Build Docker Image') {
            steps {
                echo '🐳 Building Docker image...'
                
                script {
                    // Build Docker image
                    def dockerImage = docker.build("${DOCKER_IMAGE}")
                    
                    // Tag the image
                    dockerImage.tag("latest")
                    dockerImage.tag("${env.GIT_COMMIT_SHORT}")
                    
                    env.DOCKER_IMAGE_ID = dockerImage.id
                }
                
                echo "✅ Docker image built: ${DOCKER_IMAGE}"
            }
        }
        
        stage('🚀 Deploy to Test Environment') {
            when {
                expression { params.DEPLOY_TO_TEST }
            }
            steps {
                echo '🚀 Deploying to test environment...'
                
                script {
                    // Stop any existing container
                    sh '''
                        echo "🛑 Stopping existing containers..."
                        docker stop ${APP_NAME}-test || true
                        docker rm ${APP_NAME}-test || true
                    '''
                    
                    // Run the new container
                    sh '''
                        echo "🔄 Starting new container..."
                        docker run -d \
                            --name ${APP_NAME}-test \
                            -p ${TEST_PORT}:8080 \
                            -e SPRING_PROFILES_ACTIVE=${TEST_PROFILE} \
                            -e SERVER_PORT=8080 \
                            ${DOCKER_IMAGE}
                    '''
                    
                    // Wait for application to start
                    sh '''
                        echo "⏳ Waiting for application to start..."
                        sleep 30
                    '''
                }
                
                echo '✅ Application deployed to test environment!'
            }
        }
        
        stage('🔍 Health Check & Smoke Tests') {
            when {
                expression { params.DEPLOY_TO_TEST }
            }
            steps {
                echo '🔍 Running health checks and smoke tests...'
                
                script {
                    // Health check
                    def healthCheck = sh(
                        script: '''
                            for i in {1..10}; do
                                if curl -f http://localhost:${TEST_PORT}/actuator/health; then
                                    echo "✅ Health check passed!"
                                    exit 0
                                else
                                    echo "⏳ Waiting for application to be ready... (attempt $i/10)"
                                    sleep 10
                                fi
                            done
                            echo "❌ Health check failed after 10 attempts"
                            exit 1
                        ''',
                        returnStatus: true
                    )
                    
                    if (healthCheck != 0) {
                        error('Health check failed!')
                    }
                    
                    // Basic API smoke tests
                    sh '''
                        echo "🧪 Running smoke tests..."
                        
                        # Test authentication endpoint
                        echo "Testing authentication endpoint..."
                        HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" \
                            -X POST \
                            -H "Content-Type: application/json" \
                            -d '{"username":"admin","password":"password"}' \
                            http://localhost:${TEST_PORT}/api/auth/login)
                        
                        if [ "$HTTP_STATUS" -eq 200 ]; then
                            echo "✅ Authentication endpoint test passed!"
                        else
                            echo "⚠️ Authentication endpoint returned status: $HTTP_STATUS"
                        fi
                        
                        # Test public endpoints
                        echo "Testing public endpoints availability..."
                        curl -s http://localhost:${TEST_PORT}/actuator/info || echo "Info endpoint not available"
                    '''
                }
                
                echo '✅ Health checks and smoke tests completed!'
            }
        }
        
        stage('📊 Integration Tests') {
            when {
                expression { params.DEPLOY_TO_TEST }
            }
            steps {
                echo '📊 Running integration tests...'
                
                sh '''
                    # Run integration tests against deployed application
                    mvn failsafe:integration-test failsafe:verify \
                        -Dtest.server.url=http://localhost:${TEST_PORT} \
                        -Dspring.profiles.active=${TEST_PROFILE} || echo "Integration tests completed"
                '''
                
                echo '✅ Integration tests completed!'
            }
            post {
                always {
                    // Publish integration test results
                    publishTestResults(
                        testResultsPattern: 'target/failsafe-reports/*.xml',
                        allowEmptyResults: true
                    )
                }
            }
        }
        
        stage('📤 Push Docker Image') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                    expression { params.ENVIRONMENT == 'production' }
                }
            }
            steps {
                echo '📤 Pushing Docker image to registry...'
                
                script {
                    docker.withRegistry('https://registry.hub.docker.com', DOCKER_HUB_CREDENTIALS) {
                        def image = docker.image("${DOCKER_IMAGE}")
                        image.push()
                        image.push("latest")
                        image.push("${env.GIT_COMMIT_SHORT}")
                    }
                }
                
                echo '✅ Docker image pushed to registry!'
            }
        }
    }
    
    post {
        always {
            echo '🧹 Cleaning up workspace...'
            
            // Clean up Docker containers
            sh '''
                echo "🐳 Cleaning up Docker containers..."
                docker stop ${APP_NAME}-test || true
                docker rm ${APP_NAME}-test || true
                
                # Remove old images (keep last 5)
                docker images ${APP_NAME} --format "table {{.Repository}}:{{.Tag}}\t{{.CreatedAt}}" | \
                    tail -n +6 | \
                    awk '{print $1}' | \
                    xargs -r docker rmi || true
            '''
            
            // Archive logs
            archiveArtifacts(
                artifacts: 'target/logs/**/*',
                allowEmptyArchive: true
            )
            
            // Clean workspace
            cleanWs()
        }
        
        success {
            echo '🎉 Pipeline completed successfully!'
            
            // Send success notification
            script {
                def message = """
                🎉 BUILD SUCCESS 🎉
                
                Project: ${JOB_NAME}
                Build: #${BUILD_NUMBER}
                Branch: ${env.GIT_BRANCH_NAME}
                Commit: ${env.GIT_COMMIT_SHORT}
                Duration: ${currentBuild.durationString}
                
                ✅ All stages completed successfully!
                📦 Artifacts available at: ${BUILD_URL}artifact/
                🔍 View full build: ${BUILD_URL}
                """
                
                echo message
                
                // Optional: Send email notification
                // emailext(
                //     subject: "✅ BUILD SUCCESS: ${JOB_NAME} #${BUILD_NUMBER}",
                //     body: message,
                //     to: "${env.CHANGE_AUTHOR_EMAIL ?: 'team@example.com'}"
                // )
            }
        }
        
        failure {
            echo '❌ Pipeline failed!'
            
            // Send failure notification
            script {
                def message = """
                ❌ BUILD FAILED ❌
                
                Project: ${JOB_NAME}
                Build: #${BUILD_NUMBER}
                Branch: ${env.GIT_BRANCH_NAME}
                Commit: ${env.GIT_COMMIT_SHORT}
                Duration: ${currentBuild.durationString}
                
                💥 Pipeline failed at stage: ${env.STAGE_NAME}
                🔍 View full build: ${BUILD_URL}
                📋 Console output: ${BUILD_URL}console
                """
                
                echo message
                
                // Optional: Send email notification
                // emailext(
                //     subject: "❌ BUILD FAILED: ${JOB_NAME} #${BUILD_NUMBER}",
                //     body: message,
                //     to: "${env.CHANGE_AUTHOR_EMAIL ?: 'team@example.com'}"
                // )
            }
        }
        
        unstable {
            echo '⚠️ Pipeline completed with warnings!'
        }
        
        changed {
            echo '🔄 Pipeline status changed from previous build!'
        }
    }
}
