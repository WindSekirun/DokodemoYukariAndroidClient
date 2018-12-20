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
        sh './gradlew --no-daemon assembleDebug --stacktrace'
      }
    }
  }
  post {
    always {
      sendNotifications currentBuild.result
    }
  }
}