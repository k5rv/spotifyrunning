#!/bin/bash
#
# Creates AWS network load balancer
#
source ./utils.sh

VPC_NAME="spotifyrun"
TARGET_GROUP_NAME="spotifyrun-alb-tg"
NLB_NAME="spotifyrun-nlb"
NLB_LISTENER_NAME="spotifyrun-nlb-listener"
HEALTH_CHECK_PATH="/actuator/health"
SUBNET_PUBLIC_A_NAME="public-eu-north-1a"
SUBNET_PUBLIC_B_NAME="public-eu-north-1b"

describe_flags() {
  echo "Available flags:"
  echo "-c: create AWS Load Balancer"
}

while getopts "tcd" flag; do
  case ${flag} in
  t)
#    poll_elb_creation
#    alb_arn=$(get_target_group_alb_arn)
#    poll_elb_instance_state "$alb_arn" "active"
#    echo "$alb_arn"
#    get_elbv2_state "$alb_arn"
#    aws elbv2 describe-load-balancers --load-balancer-arns "$alb_arn"
#    get_elbv2_state "$alb_arn"
#    aws elbv2 describe-target-groups  --query "TargetGroups[].LoadBalancerArns[]" --output text

#
#    "TargetGroups": [
#            {
#                "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-north-1:690837617850:targetgroup/k8s-default-spotifyr-d46f20e49f/fedfdabc6cd2cba5",
#                "TargetGroupName": "k8s-default-spotifyr-d46f20e49f",
#                "Protocol": "HTTP",
#                "Port": 30380,
#                "VpcId": "vpc-0ee97231173f7080c",
#                "HealthCheckProtocol": "HTTP",
#                "HealthCheckPort": "traffic-port",
#                "HealthCheckEnabled": true,
#                "HealthCheckIntervalSeconds": 300,
#                "HealthCheckTimeoutSeconds": 10,
#                "HealthyThresholdCount": 2,
#                "UnhealthyThresholdCount": 2,
#                "HealthCheckPath": "/actuator/health",
#                "Matcher": {
#                    "HttpCode": "200"
#                },
#                "LoadBalancerArns": [
#                    "arn:aws:elasticloadbalancing:eu-north-1:690837617850:loadbalancer/app/spotifyrun-alb/f05eaffc1a11dd1a"
#                ],
    ;;
  c)
    poll_elb_creation
    alb_arn=$(get_target_group_alb_arn)

    target_group_arn=$(get_target_group_arn "$TARGET_GROUP_NAME")
    if [[ "$target_group_arn" == "" ]]; then
      printf "Target Group '%s' not found\n" $TARGET_GROUP_NAME
      vpc_id=$(get_vpc_id $VPC_NAME)
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
      printf "Created Target Group '%s'\n" "$target_group_arn"
    fi

    nlb_arn=$(get_nlb_arn $NLB_NAME)
    if [[ "$nlb_arn" == "" ]]; then
      printf "Load Balancer '%s' not found\n" $NLB_NAME
      subnet_public_a_id=$(get_subnet_id "$SUBNET_PUBLIC_A_NAME")
      subnet_public_b_id=$(get_subnet_id "$SUBNET_PUBLIC_B_NAME")
      nlb_arn=$(create_nlb "$NLB_NAME" "$subnet_public_a_id" "$subnet_public_b_id")
      printf "Initiated creation of Load Balancer '%s'\n" "$nlb_arn"
    fi

    nlb_listener_arn=$(create_lb_listener $NLB_LISTENER_NAME "$target_group_arn" "TCP" 80)
    terminate_if_empty "$nlb_listener_arn"
    printf "Created Load Balancer Listener '%s'\n" "$nlb_listener_arn"

    poll_elb_instance_state "$nlb_arn" "active"
    poll_elb_instance_state "$alb_arn" "active"
    aws elbv2 register-targets --target-group-arn "$target_group_arn" --targets Id="$alb_arn"
    ;;
  d)
    nlb_arn=$(get_elbv2_arn $NLB_NAME)

    listener_arn=$(get_listener_arn "$nlb_arn")
    delete_listener "$listener_arn"

    target_group_arn=$(get_target_group_arn "$TARGET_GROUP_NAME")
    delete_target_group "$target_group_arn"

    delete_elbv2 "$nlb_arn"
    ;;
  \?)
    describe_flags
    ;;
  esac
done
