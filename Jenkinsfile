/*
 *     Lyrebird, a free open-source cross-platform twitter client.
 *     Copyright (C) 2017-2018, Tristan Deloche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
                sh 'touch .stalonetrayrc && nohup stalonetray &'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn clean test -Djava.awt.headless=false'
            }
        }
        stage('Code quality') {
            steps {
                sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.4.0.905:sonar -Dsonar.host.url=https://sonar.tristan.moe'
            }
        }
        stage('PackageInstall') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('Archive artifacts') {
            steps {
                archiveArtifacts 'lyrebird/target/lyrebird*.jar'
            }
        }
    }
    post('Cleanup') {
        always {
            sh 'kill -15 $(pgrep stalonetray) && rm .stalonetrayrc && kill -15 $(pgrep Xvfb)'
        }
    }
}
