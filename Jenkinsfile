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
        sh 'touch ~/.stalonetrayrc && nohup stalonetray & sleep $SLEEP_T'
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
        sh 'kill -15 $(pgrep Xvfb) && kill -15 $(pgrep stalonetray)'
      }
    }
    stage('Archive artifacts') {
      steps {
        archiveArtifacts 'target/lyrebird*.jar'
      }
    }
  }
}
