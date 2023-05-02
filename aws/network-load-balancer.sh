#!/bin/bash
#
# Creates AWS network load balancer

CLUSTER_NAME="spotifyrun"
VPC_NAME="spotifyrun"
NLB_NAME="spotifyrun-nlb"
ALB_NAME="spotifyrun-alb"
NLB_LISTENER_NAME="spotifyrun-nlb-listener"
TARGET_GROUP_NAME="spotifyrun-alb-tg"
#HEALTH_CHECK_PATH="/app/actuator/health"
HEALTH_CHECK_PATH="/actuator/health"
SUBNET_PUBLIC_A_NAME="public-eu-north-1a"
SUBNET_PUBLIC_B_NAME="public-eu-north-1b"
#EIP_NLB_A_NAME="eu-north-1a-nlb"
#EIP_NLB_B_NAME="eu-north-1b-nlb"

describe_flags() {
  echo "Available flags:"
  echo "-c: create AWS network load balancer for cluster $CLUSTER_NAME"
}

get_vpc_id() {
  local vpc_name=$1
  vpc_id=$(aws ec2 describe-vpcs --filter Name=tag:Name,Values="$vpc_name" --query "Vpcs[].VpcId" --output text)
  echo "$vpc_id"
}

get_subnet_id() {
  local subnet_name="$1"
  subnet_id=$(aws ec2 describe-subnets \
    --filter "Name=tag:Name,Values=$subnet_name" \
    --query 'Subnets[].SubnetId' \
    --output text)
  echo "$subnet_id"
}

get_eip_id() {
  local eip_name="$1"
  eip_id=$(aws ec2 describe-addresses \
    --filter "Name=tag:Name,Values=$eip_name" \
    --query Addresses[].AllocationId \
    --output text)
  echo "$eip_id"
}

while getopts "tc" flag; do
  case ${flag} in
  t) ;;
  c)
    echo "Looking for alb"
    alb_arn=$(aws elbv2 describe-target-groups \
      --query "TargetGroups[].LoadBalancerArns" \
      --output text)
    echo "Found alb: $alb_arn"

    echo "Looking for target group $TARGET_GROUP_NAME"
    target_group_arn=$(aws elbv2 describe-target-groups --names $TARGET_GROUP_NAME \
      --query "TargetGroups[].TargetGroupArn" \
      --output text)

    if [[ "$target_group_arn" == "" ]]; then
      echo "Target group $TARGET_GROUP_NAME not found"
      vpc_id=$(get_vpc_id $VPC_NAME)
      echo "Creating target group $TARGET_GROUP_NAME"
      target_group_arn=$(aws elbv2 create-target-group \
        --name $TARGET_GROUP_NAME \
        --target-type alb \
        --vpc-id "$vpc_id" \
        --protocol "TCP" \
        --port 80 \
        --health-check-protocol "HTTP" \
        --health-check-port "80" \
        --health-check-path "$HEALTH_CHECK_PATH" \
        --health-check-interval-seconds 300 \
        --health-check-timeout-seconds 10 \
        --healthy-threshold-count 2 \
        --unhealthy-threshold-count 2 \
        --matcher "HttpCode=200" \
        --tags "[{\"Key\":\"Name\",\"Value\":\"$TARGET_GROUP_NAME\"}]" \
        --query "TargetGroups[].TargetGroupArn" \
        --output text)
    fi
    echo "Target group: $target_group_arn"

    echo "Looking for network load balancer $NLB_NAME"
    nlb_arn=$(aws elb describe-load-balancers \
      --load-balancer-names $NLB_NAME \
      --query "LoadBalancers[].LoadBalancerArn" \
      --output text)

    #          --subnet-mappings SubnetId="$subnet_public_a_id",AllocationId="$eip_nlb_a_id" SubnetId="$subnet_public_b_id",AllocationId="$eip_nlb_b_id" \

    if [[ "$nlb_arn" == "" ]]; then
      echo "Load balancer $NLB_NAME not found"
      subnet_public_a_id=$(get_subnet_id "$SUBNET_PUBLIC_A_NAME")
      subnet_public_b_id=$(get_subnet_id "$SUBNET_PUBLIC_B_NAME")
      #      eip_nlb_a_id=$(get_eip_id "$EIP_NLB_A_NAME")
      #      eip_nlb_b_id=$(get_eip_id "$EIP_NLB_B_NAME")
      echo "Creating load balancer $NLB_NAME"
      nlb_arn=$(
        aws elbv2 create-load-balancer \
          --name "$NLB_NAME" \
          --scheme internet-facing \
          --subnets "$subnet_public_a_id" "$subnet_public_b_id" \
          --tags "[{\"Key\":\"Name\",\"Value\":\"$NLB_NAME\"}]" \
          --type "network" \
          --query "LoadBalancers[].LoadBalancerArn" \
          --output text
      )
    fi
    echo "Load balancer: $nlb_arn"

    echo "Creating listener for $NLB_NAME"
    nlb_listener_arn=$(aws elbv2 create-listener \
      --load-balancer-arn "$nlb_arn" \
      --protocol "TCP" \
      --port 80 \
      --default-actions Type=forward,TargetGroupArn="$target_group_arn" \
      --tags "[{\"Key\":\"Name\",\"Value\":\"$NLB_LISTENER_NAME\"}]" \
      --query "Listeners[].ListenerArn" \
      --output text)

    echo "Load balancer listener: $nlb_listener_arn"

    echo "Registering $ALB_NAME as a target in $TARGET_GROUP_NAME"
    aws elbv2 register-targets \
      --target-group-arn "$target_group_arn" \
      --targets Id="$alb_arn"
    ;;
  \?)
    describe_flags
    ;;
  esac
done
