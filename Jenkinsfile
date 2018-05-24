pipeline {
  agent any
  stages {
    stage('Environment') {
      steps {
        sh 'nohup Xvfb & sleep 3'
        sh 'touch ~/.stalonetrayrc && nohup stalonetray & sleep 3'
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
