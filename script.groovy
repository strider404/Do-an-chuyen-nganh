def checkoutCode() {
    checkout scm
    echo "Checked out source code"
}

def buildAndPushDockerImages() {
    withCredentials([usernamePassword(credentialsId: "docker-hub", usernameVariable: "DOCKER_USER",
    passwordVariable: "DOCKER_PASS")]) {
        sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"
        def services = ["user-service", "restaurant-service", "order-service", "payment-service", "delivery-service", "notification-service", "frontend", "gateway"]
        services.each { svc ->
            echo "Building and pushing image for: ${svc}"
            sh "docker build -t ${DOCKER_HUB_USER}/osm-${svc}:${IMAGE_TAG} ./${svc}"
            sh "docker push ${DOCKER_HUB_USER}/osm-${svc}:${IMAGE_TAG}"
        }
    }
}

def deployToKubernetes() {
    echo "Deploying all Kubernetes manifests from ${K8S_PATH}/"
    sh "kubectl apply -f ${K8S_PATH}/namespace.yaml"
    sh "kubectl apply -f ${K8S_PATH}/"
    echo "Deployment completed successfully"
}

def verifyDeployment() {
    echo "Verifying deployment..."
    sh "kubectl get pods -n osm"
    sh "kubectl get services -n osm"
}

return this
