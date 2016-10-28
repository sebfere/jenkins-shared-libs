
def echoStage(String stageName) {
    echo """
*************************************************
************************************************* ${stageName.toUpperCase()}
*************************************************
"""
}


def echoBuildContext() {
    echo """
jobName            ${env.JOB_NAME}
buildNumber        ${env.BUILD_NUMBER}
nodeName           ${env.NODE_NAME}

branch             ${env.BRANCH_NAME}
"""
}
