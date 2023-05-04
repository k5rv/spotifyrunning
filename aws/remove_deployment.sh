#!/bin/bash
#
# Removes infrastructure and application
#
sh ./dns.sh -d
sh ./nlb.sh -d
kubectl delete -f "../k8s/eks/services/spotifyrun/"
sh ./eks.sh -d
sh ./rds.sh -d
sh ./vpc.sh -d
