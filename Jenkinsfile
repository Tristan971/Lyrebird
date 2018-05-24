pipeline {
  agent any
  stages {
    stage('Environment') {
      steps {
        sh 'export DISPLAY=:99.0 && nohup Xvfb :99 -screen 0 1024x768x24 & sleep 3'
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
