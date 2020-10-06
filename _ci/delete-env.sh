export RELEASE_NAME=$NAMESPACE
export RELEASE_INGRESS_NAME="${NAMESPACE}-ingress"

#
# Deletes the environment
#
function deleteEnv {
  echo "=========================== Deleting the environment ==========================="

  # remove environments
  helm uninstall $RELEASE_NAME --namespace $NAMESPACE
  helm uninstall $RELEASE_INGRESS_NAME --namespace $NAMESPACE

  # remove Route53 entry
  aws route53 change-resource-record-sets --hosted-zone-id $HOSTED_ZONE_ID --change-batch "$(get_route53_json "DELETE")"

  # remove namespace
  kubectl delete namespace $NAMESPACE
}

if [[ $KEEP_ENV = false ]]; then
    deleteEnv
fi;
