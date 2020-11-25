if [[ "$TRAVIS_BRANCH" = "develop" ]]; then
  NAMESPACE="develop-share"
else
  NAMESPACE="travis-share-$TRAVIS_BUILD_NUMBER"
fi

source _ci/init_tag.sh

export HOST="${NAMESPACE}.${HOSTED_ZONE}"
export RELEASE_NAME=$NAMESPACE
export RELEASE_INGRESS_NAME="${NAMESPACE}-ingress"
export ALFRESCO_REPO_IMAGE="alfresco-content-repository"
export ALFRESCO_SHARE_IMAGE="alfresco-share"

#
# Determine if the current branch is develop or not
#
function isBranchDevelop() {
  if [ "${TRAVIS_BRANCH}" = "develop" ]; then
    return 0
  else
    return 1
  fi
}

#
# Determine if the develop environment is up and running
#
function isDevelopUp() {
  status_code=$(curl --write-out %{http_code} --silent --output /dev/null $DEVELOP_URL)
  if [ "$status_code" -eq 200 ] ; then
    return 0
  else
    return 1
  fi
}

function updateDevelopEnv()  {
  echo "Update develop"

  # add the helm repos
  helm repo add alfresco-incubator https://kubernetes-charts.alfresco.com/incubator
  helm repo add alfresco-stable https://kubernetes-charts.alfresco.com/stable
  helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx


  # update the helm repos
  helm repo update
  
  # repository.replicaCount=1 - this is a temporary fix until issues on clusterd environments are fixed.
  helm upgrade --install $RELEASE_NAME -f _ci/6.2.N_values.yaml alfresco-incubator/alfresco-content-services --version 5.0.0-M2 --namespace $NAMESPACE
}


#
# Returns the Route53 json payload ChangeResourceRecordSets API call
# Can take one argument that specifies the action: "CREATE", "DELETE", "UPSERT"; if none
# specified, it defaults to "CREATE"
#
function get_route53_json {
  local out
  local action
  local path="_ci/route53.json"

  if [[ ( -z "$1" ) || ( "$1" != "CREATE" && "$1" != "DELETE" && "$1" != "UPSERT" ) ]]; then
    action="CREATE"
  else
    action=$1
  fi

  out=$(sed -e "s/\$Action/$action/" \
    -e "s/\$Name/${HOST}/" \
    -e "s/\$Value/${ELBADDRESS}/" $path)

  echo $out
}

#
# Creates the environment
#
function createEnv {
  echo "=========================== Creating the environment ==========================="

  # create k8s namespace
  kubectl create namespace $NAMESPACE

  # add the helm repos
  helm repo add alfresco-incubator https://kubernetes-charts.alfresco.com/incubator
  helm repo add alfresco-stable https://kubernetes-charts.alfresco.com/stable
  helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx

  # update the helm repos
  helm repo update

  # create secret on k8s namespace
  kubectl create secret docker-registry quay-registry-secret --docker-server=quay.io --docker-username=${QUAY_USERNAME} --docker-password=${QUAY_PASSWORD} --namespace $NAMESPACE

  # update the ingress values file
  sed -i 's/REPLACEME_RELEASE_INGRESS/'"$RELEASE_INGRESS_NAME"'/g' _ci/values-for-ingress-travis-env.yaml
  sed -i 's/REPLACEME_NAMESPACE/'"$NAMESPACE"'/g' _ci/values-for-ingress-travis-env.yaml

  # apply cluster role bindings
  kubectl apply -f _ci/values-for-ingress-travis-env.yaml

  # install ingress
  helm upgrade --install $RELEASE_INGRESS_NAME ingress-nginx/ingress-nginx --version 2.16.0 \
          --set controller.scope.enabled=true \
          --set controller.scope.namespace=$NAMESPACE \
          --set rbac.create=true \
          --set controller.config."proxy-body-size"="100m" \
          --set controller.service.targetPorts.https=80 \
          --set controller.service.annotations."service\.beta\.kubernetes\.io/aws-load-balancer-backend-protocol"="http" \
          --set controller.service.annotations."service\.beta\.kubernetes\.io/aws-load-balancer-ssl-ports"="https" \
          --set controller.service.annotations."service\.beta\.kubernetes\.io/aws-load-balancer-ssl-cert"=$SSL_CERT \
          --set controller.service.annotations."external-dns\.alpha\.kubernetes\.io/hostname"=$HOST \
          --set controller.service.annotations."service\.beta\.kubernetes\.io/aws-load-balancer-ssl-negotiation-policy"="ELBSecurityPolicy-TLS-1-2-2017-01" \
          --set controller.publishService.enabled=true \
          --namespace $NAMESPACE

  # install ACS chart
  # repository.replicaCount=1 - this is a temporary fix until issues on clusterd environments are fixed.
  helm upgrade --install $RELEASE_NAME -f _ci/6.2.N_values.yaml alfresco-incubator/alfresco-content-services --version 5.0.0-M2 --namespace $NAMESPACE

  # get ELB address required for Route53 entry
  export ELBADDRESS=$(kubectl get services $RELEASE_INGRESS_NAME-ingress-nginx-controller --namespace=$NAMESPACE -o jsonpath={.status.loadBalancer.ingress[0].hostname})

  # get hosted zone id for Alias Target
  IFS='-' read -r -a array <<< "$ELBADDRESS"
  export ALIAS_HOSTED_ZONE_ID=$(aws elb describe-load-balancers --load-balancer-name ${array[0]} | jq -r '.LoadBalancerDescriptions | .[] | .CanonicalHostedZoneNameID')

  # get Route53 hosted zone id
  export HOSTED_ZONE_ID=$(aws route53 list-hosted-zones-by-name --dns-name $HOSTED_ZONE | jq -r '.HostedZones | .[] | .Id')

  # create Route53 entry
  aws route53 change-resource-record-sets --hosted-zone-id $HOSTED_ZONE_ID --change-batch "$(get_route53_json  "UPSERT")"
}

#
# Before running the tests make sure that all pods are green
#
function wait_for_pods {
  # counters
  PODS_COUNTER=0
  # counters limit
  PODS_COUNTER_MAX=120
  # sleep seconds
  PODS_SLEEP_SECONDS=10

  namespace=$1

  echo "Validate that all the pods in the deployment are ready"
  while [ "$PODS_COUNTER" -lt "$PODS_COUNTER_MAX" ]; do
    pendingpodcount=$(kubectl get pods --namespace "$namespace" | awk '{print $2}' | grep -c '0/1' || true)
    if [ "$pendingpodcount" -eq 0 ]; then
      runningPods=$(kubectl get pods --namespace "$namespace")
      echo "All pods are Running and Ready!"
      echo "$runningPods"
      break
    fi
    PODS_COUNTER=$((PODS_COUNTER + 1))
    echo "$pendingpodcount pods are not yet ready - sleeping $PODS_SLEEP_SECONDS seconds - counter $PODS_COUNTER"
    sleep "$PODS_SLEEP_SECONDS"
    continue
  done
  if [ "$PODS_COUNTER" -ge "$PODS_COUNTER_MAX" ]; then
    failedPods=$(kubectl get pods --namespace "$namespace" | grep '0/1' | awk '{print $1}')
    echo "\nThe following pods were not ready:\n"
    for failedpod in $failedPods; do
      echo "$failedpod"
    done
    for failedpod in $failedPods; do
      echo "Description for $failedpod :\n"
      kubectl describe pod $failedpod --namespace "$namespace"
    done
    echo "Pods did not start - exit"
    kubectl get pods --namespace "$namespace"
    exit 1
  fi
}

# Main
if $(isBranchDevelop); then
  echo "On branch develop"
  SHARE_TAG_NAME=$TAG_NAME
  if [ "${TRAVIS_EVENT_TYPE}" != "pull_request" ]; then
    if $(isDevelopUp); then
      echo "Update develop environment"
      updateDevelopEnv
      wait_for_pods $NAMESPACE
    else
      echo "Create develop environment"
      createEnv
      wait_for_pods $NAMESPACE
    fi
  else
    echo "Create PR env environment"
    createEnv
    wait_for_pods $NAMESPACE
  fi
else
  echo "On development branch"
  SHARE_TAG_NAME=$TAG_NAME
  createEnv
  wait_for_pods $NAMESPACE
fi
