pipeline {
    agent any

    environment {
        DISPLAY = ':99'
        SLEEP_T = '3'
        MAVEN_OPTS = '-XX:+TieredCompilation -XX:TieredStopAtLevel=1'
    }

    stages {
        stage('Environment') {
            steps {
                sh 'nohup Xvfb $DISPLAY -screen 0 1024x768x24 & sleep $SLEEP_T'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
        }
        stage('Package') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('Clean environment') {
            steps {
                sh 'kill -15 $(pgrep Xvfb)'
            }
        }
        stage('Code quality') {
            steps {
                sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.4.0.905:sonar -Dsonar.host.url=https://sonar.tristan.moe'
            }
        }
        stage('Archive artifacts') {
            steps {
                archiveArtifacts 'target/lyrebird*.jar'
            }
        }
    }
}
