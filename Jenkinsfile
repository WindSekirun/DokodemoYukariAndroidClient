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
      }
    }
  }
  post {
    always {
      publishHTML(target: [reportDir:'app/build/reports/detekt/', reportFiles: 'detekt.html', reportName: 'Detekt report'])
      sendNotifications currentBuild.result
    }
  }
}