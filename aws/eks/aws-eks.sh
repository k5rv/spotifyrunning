#!/bin/bash
#
# Creates AWS EKS cluster, uses cluster.yaml file as a configuration

PROJECT="spotifyrun"
CONFIGURATION_FILE="cluster.yaml"

describe_flags() {
  echo "Available flags:"
  echo "-c: create cluster $PROJECT using configuration file $CONFIGURATION_FILE"
  echo "-d: delete cluster $PROJECT"
}

while getopts "cd" flag; do
  case ${flag} in
  c)
    echo "Creating cluster using configuration file $CONFIGURATION_FILE"
    eksctl create cluster --config-file $CONFIGURATION_FILE
    ;;
  d)
    echo "Deleting cluster $PROJECT"
    eksctl delete cluster --name=$PROJECT
    ;;
  \?)
    describe_flags
    ;;
  esac
done
