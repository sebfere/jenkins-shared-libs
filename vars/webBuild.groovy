

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String jobName
    String buildNumber
    String nodeName

    String artifact
    String version

    String branch
    String scmConnection
    boolean scmSVN = false
    boolean scmGIT = false
    boolean featureMode = false
    boolean releaseMode = false


    node('web1') {
        stage('checkout') {
            echoStage 'checkout'

            git branch: 'master', url: 'https://github.com/tanou73/AngularTodo'
        }

        stage('init') {
            jobName = env.JOB_NAME
            buildNumber = env.BUILD_NUMBER
            nodeName = env.NODE_NAME
            artifact = 'AngularTodo'
        
                echo """
jobName            $jobName
buildNumber        $buildNumber
nodeName           $nodeName

branch             $branch

artifact           $artifact
version            $version
"""

            wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                retry(2) {
                    sh 'npm install'
                }
                sh 'bower install'
            }
        }

        stage('build') {
            milestone label: 'build'
            echoStage 'build'
            
            wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                sh 'grunt build'
            }
        }
        
        stage('unit-test') {
            milestone label: 'unit-test'
            echoStage 'unit-test'

            wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                // sh 'grunt test'
                echo 'grunt test'
                sleep 10
            }
        }
        
        stage('integration-test') {
            milestone label: 'integration-test'
            echoStage 'integration-test'

            // sh 'grunt e2e'
            
            retry(2) {
                echo 'Launch test environment'
            }
            parallel (
                'smoke' : { echo 'Smoke tests start'; sleep 10; echo 'Smoke tests finish' },
                'front_e2e' :  {echo 'Selenium tests start'; sleep 40; echo 'Selenium tests finish' },
                'back_e2e' : { echo 'Back tests start'; sleep 30; echo 'Back tests finish' }
            )
        }
    }
}