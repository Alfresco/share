if [[ "$TRAVIS_BRANCH" = "develop" ]]; then
  NAMESPACE="develop-share"
else
  NAMESPACE="travis-share-$TRAVIS_BUILD_NUMBER"
fi

export HOST="${NAMESPACE}.${HOSTED_ZONE}"
export RELEASE_NAME=$NAMESPACE
export RELEASE_INGRESS_NAME="${NAMESPACE}-ingress"

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
# Deletes the environment
#
function deleteEnv {
  echo "=========================== Deleting the environment ==========================="

  export ELBADDRESS=$(kubectl get services $RELEASE_INGRESS_NAME-ingress-nginx-controller --namespace=$NAMESPACE -o jsonpath={.status.loadBalancer.ingress[0].hostname})

  # get Route53 hosted zone id
  export HOSTED_ZONE_ID=$(aws route53 list-hosted-zones-by-name --dns-name $HOSTED_ZONE | jq -r '.HostedZones | .[] | .Id')

  # remove environments
  helm delete $RELEASE_NAME --namespace $NAMESPACE
  helm delete $RELEASE_INGRESS_NAME --namespace $NAMESPACE

  # remove Route53 entry
  aws route53 change-resource-record-sets --hosted-zone-id $HOSTED_ZONE_ID --change-batch "$(get_route53_json "DELETE")"

  # remove namespace
  kubectl delete namespace $NAMESPACE
}

if [[ $KEEP_ENV = false && "$TRAVIS_BRANCH" != "develop" ]]; then
  deleteEnv
else
  echo "Keeping environment $HOST";
  echo "Please delete it manually when you will no longer need it!"
fi;
