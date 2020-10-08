export RELEASE_NAME=$NAMESPACE
export RELEASE_INGRESS_NAME="${NAMESPACE}-ingress"
export ALFRESCO_REPO_IMAGE="alfresco-content-repository"
export ALFRESCO_SHARE_IMAGE="alfresco-share"
export DEVELOP_URL="https://develop.dev.alfresco.me"

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

  echo "RELEASE_NAME="$RELEASE_NAME

  helm uninstall $RELEASE_NAME --namespace $NAMESPACE

  # add the helm repos
  helm repo add alfresco-incubator https://kubernetes-charts.alfresco.com/incubator
  helm repo add alfresco-stable https://kubernetes-charts.alfresco.com/stable
  helm repo add stable https://kubernetes-charts.storage.googleapis.com

  # update the helm repos
  helm repo update

  helm upgrade --install $RELEASE_NAME alfresco-incubator/alfresco-content-services --version 5.0.0 \
          --set externalPort="443" \
          --set externalProtocol="https" \
          --set externalHost=$HOST \
          --set persistence.enabled=true \
          --set persistence.baseSize=10Gi \
          --set persistence.storageClass.enabled=true \
          --set persistence.storageClass.name="nfs-sc" \
          --set activemq.persistence.mountPath="/opt/activemq/data/${NAMESPACE}" \
          --set global.alfrescoRegistryPullSecrets=quay-registry-secret \
          --set repository.adminPassword="${ADMIN_PWD}" \
          --set repository.image.repository="quay.io/alfresco/${ALFRESCO_REPO_IMAGE}" \
          --set repository.image.tag="${REPO_TAG_NAME}" \
          --set share.image.repository="quay.io/alfresco/${ALFRESCO_SHARE_IMAGE}" \
          --set share.image.tag="${SHARE_TAG_NAME}" \
          --namespace $NAMESPACE
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
  helm repo add stable https://kubernetes-charts.storage.googleapis.com

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
  helm upgrade --install $RELEASE_INGRESS_NAME stable/nginx-ingress \
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
  helm upgrade --install $RELEASE_NAME alfresco-incubator/alfresco-content-services --version 5.0.0 \
          --set externalPort="443" \
          --set externalProtocol="https" \
          --set externalHost=$HOST \
          --set persistence.enabled=true \
          --set persistence.baseSize=10Gi \
          --set persistence.storageClass.enabled=true \
          --set persistence.storageClass.name="nfs-sc" \
          --set global.alfrescoRegistryPullSecrets=quay-registry-secret \
          --set repository.adminPassword="${ADMIN_PWD}" \
          --set repository.image.repository="quay.io/alfresco/${ALFRESCO_REPO_IMAGE}" \
          --set repository.image.tag="${REPO_TAG_NAME}" \
          --set share.image.repository="quay.io/alfresco/${ALFRESCO_SHARE_IMAGE}" \
          --set share.image.tag="${SHARE_TAG_NAME}" \
          --set repository.environment.JAVA_OPTS="-Dalfresco.restApi.basicAuthScheme=true -Dsolr.base.url=/solr -Dsolr.secureComms=none -Dindex.subsystem.name=solr6 -Dalfresco.cluster.enabled=true -Ddeployment.method=HELM_CHART -Dtransform.service.enabled=true -Xms2000M -Xmx2000M" \
          --namespace $NAMESPACE

  # get ELB address required for Route53 entry
  export ELBADDRESS=$(kubectl get services $RELEASE_INGRESS_NAME-nginx-ingress-controller --namespace=$NAMESPACE -o jsonpath={.status.loadBalancer.ingress[0].hostname})

  # get hosted zone id for Alias Target
  IFS='-' read -r -a array <<< "$ELBADDRESS"
  export ALIAS_HOSTED_ZONE_ID=$(aws elb describe-load-balancers --load-balancer-name ${array[0]} | jq -r '.LoadBalancerDescriptions | .[] | .CanonicalHostedZoneNameID')

  # get Route53 hosted zone id
  export HOSTED_ZONE_ID=$(aws route53 list-hosted-zones-by-name --dns-name $HOSTED_ZONE | jq -r '.HostedZones | .[] | .Id')

  # create Route53 entry
  aws route53 change-resource-record-sets --hosted-zone-id $HOSTED_ZONE_ID --change-batch "$(get_route53_json  "UPSERT")"
}

# Main
if $(isBranchDevelop); then
  echo "On branch develop"

  export NAMESPACE="develop"
  export HOST="${NAMESPACE}.${HOSTED_ZONE}"
  export RELEASE_NAME=$NAMESPACE
  export RELEASE_INGRESS_NAME="${NAMESPACE}-ingress"

  if $(isDevelopUp); then
    updateDevelopEnv
  else
    echo "Create develop environment"
    createEnv
  fi
else
  echo "On development branch"
  createEnv
fi
