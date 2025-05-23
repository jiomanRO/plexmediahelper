pipeline {
    agent any

    environment {
        PORTAINER_URL = <url>
        PORTAINER_USER = <user>
        PORTAINER_PASS = <pass>
        STACK_NAME = 'plexmediahelper'
    }

    stages {
        stage('Build & Deploy') {
            steps {
                script {
                    // Authenticate and get token
                    writeFile file: 'portainer_auth.json', text: """{"Username":"${PORTAINER_USER}","Password":"${PORTAINER_PASS}"}"""

                    def token = sh(
                            script: """
            curl -s -k -X POST $PORTAINER_URL/api/auth \\
                -H "Content-Type:application/json" \\
                -d @portainer_auth.json \\
            | jq -r .jwt
        """,
                            returnStdout: true
                    ).trim()

                    if (!token) {
                        error "Failed to retrieve JWT token"
                    }

                    // Fetch stacks JSON as a string
                    def stacksJson = sh(
                            script: """
            curl -s -k -H "Authorization: Bearer ${token}" $PORTAINER_URL/api/stacks
        """,
                            returnStdout: true
                    ).trim()

                    // Parse JSON into LazyMap inside this script block, then immediately convert to simple map
                    def stacksRaw = new groovy.json.JsonSlurper().parseText(stacksJson)
                    // Convert LazyMap to HashMap to make it serializable
                    def stacks = stacksRaw.collect { new HashMap(it) }
                    def stack = stacks.find { it.get('Name') == STACK_NAME }

                    if (!stack) {
                        error "Stack '${STACK_NAME}' not found"
                    }

                    echo "Found stack ${stack.Name} with ID ${stack.Id}"

                    // Fetch stack details JSON string
                    def stackDetailsResponse = sh(
                            script: """
            curl -s -k -H "Authorization: Bearer ${token}" ${PORTAINER_URL}/api/stacks/${stack.Id}
        """,
                            returnStdout: true
                    ).trim()

                    // Convert stack details LazyMap to HashMap as well
                    def stackDetailsRaw = new groovy.json.JsonSlurper().parseText(stackDetailsResponse)
                    def stackDetails = new HashMap(stackDetailsRaw)

                    def updatePayload = [
                            Name: stackDetails.Name,
                            StackFileContent: stackDetails.StackFileContent,
                            Env: stackDetails.Env ?: [],
                            Prune: false,
                            EndpointId: stackDetails.EndpointId,
                            SwarmID: stackDetails.SwarmId ?: "",
                            EntryPoint: stackDetails.EntryPoint
                    ]

                    def updateJson = groovy.json.JsonOutput.toJson(updatePayload)
                    writeFile file: 'stack_update.json', text: updateJson

                    def putResponse = sh(
                            script: """
            curl -v -s -k -X PUT \\
                -H "Authorization: Bearer ${token}" \\
                -H "Content-Type: application/json" \\
                -d @stack_update.json \\
                ${PORTAINER_URL}/api/stacks/${stack.Id}
        """,
                            returnStdout: true
                    ).trim()

                    echo "Redeploy response: ${putResponse}"
                }
            }
        }
    }
}
