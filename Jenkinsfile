@Library('jenkins-shared-library')_
pipeline {
  agent any
  stages {
    stage ('Start') {
      steps {
        sendNotifications 'STARTED'
      }
    }
    stage('assembleDebug') {
      steps {
        sh './gradlew  assembleDebug --stacktrace'
      }
    }
    stage('Static Analysis') {
          steps {
            sh './gradlew detekt'
            publishHTML(target: [reportDir:'BaseApp/build/reports/detekt/', reportFiles: 'detekt.html', reportName: 'Detekt report'])
          }
    }
  }
  post {
    always {
      sendNotifications currentBuild.result
    }
  }
}