
import ch.serial.jenkins.PipelineHelper

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipelineHelper = new PipelineHelper()

    node('web') {
        stage('init') {
            pipelineHelper.echoStage 'init'

            checkout scm

            pipelineHelper.echoBuildContext()

            wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                sh 'npm install'
                sh 'bower install'
            }
        }

        stage('build') {
            pipelineHelper.echoStage 'build'

            wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                sh 'grunt build'
            }
        }

        stage('unit-test') {
            pipelineHelper.echoStage 'unit-test'

            wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                sh 'grunt test'
            }
        }
    }

}
