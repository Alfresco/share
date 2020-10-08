#!/bin/bash

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

