#!/bin/bash
#
# Creates AWS resources: VPC, internet gateway, subnets, route tables, security group

PROJECT="spotifyrun"
REGION="eu-north-1"
VPC_CIDR_BLOCK="10.0.0.0/16"
AVAILABILITY_ZONES=("eu-north-1a" "eu-north-1b")
NETWORK_BORDER_GROUP="eu-north-1"
SUBNET_CIDR_BLOCKS=("10.0.1.0/24" "10.0.2.0/24" "10.0.3.0/24" "10.0.4.0/24")

describe_flags() {
  echo "Available flags:"
  echo "-d: delete $PROJECT project VPC and its dependencies"
  echo "-c: create $PROJECT project VPC and its dependencies"
}

create_vpc() {
  echo "Creating VPC with CIDR block $VPC_CIDR_BLOCK in $REGION"
  VPC_ID=$(aws ec2 create-vpc \
    --cidr-block $VPC_CIDR_BLOCK \
    --region $REGION \
    --tag-specification "ResourceType=vpc,Tags=[{Key=Name,Value=$PROJECT},{Key=Project,Value=$PROJECT},{Key=kubernetes.io/cluster/$PROJECT,Value=owned}]" \
    --query "Vpc.{VpcId:VpcId}" \
    --output text)
  echo "Enabling $VPC_ID DNS hostnames"
  aws ec2 modify-vpc-attribute --vpc-id "$VPC_ID" --enable-dns-hostnames "{\"Value\":true}"
}

get_vpc_id() {
  VPC_ID=$(aws ec2 describe-vpcs \
    --filter Name=tag:Name,Values=$PROJECT \
    --query "Vpcs[].VpcId" \
    --output text)
  echo "$VPC_ID"
}

delete_vpc() {
  echo "Deleting VPC id in project $PROJECT"
  VPC_ID=$(get_vpc_id)
  aws ec2 delete-vpc --vpc-id "$VPC_ID"
  echo "Deleted $VPC_ID"
}

get_internet_gateway_id() {
  local internet_gateway_id
  internet_gateway_id=$(aws ec2 describe-internet-gateways \
    --filter Name=tag:Name,Values=$PROJECT \
    --query "InternetGateways[].InternetGatewayId" \
    --output text)
  echo "$internet_gateway_id"
}

#Name=attachment.state,Values=available,detach

create_internet_gateway() {
  echo "Creating internet gateway in $REGION"
  INTERNET_GATEWAY_ID=$(aws ec2 create-internet-gateway \
    --region $REGION \
    --tag-specifications "ResourceType=internet-gateway,Tags=[{Key=Name,Value=$PROJECT},{Key=Project,Value=$PROJECT}]" \
    --query 'InternetGateway.{InternetGatewayId:InternetGatewayId}' \
    --output text)
  echo "Created $INTERNET_GATEWAY_ID"
}

attach_internet_gateway() {
  local vpc_id
  local igw_id
  vpc_id=$(get_vpc_id)
  igw_id=$(get_internet_gateway_id)
  echo "Attaching $INTERNET_GATEWAY_ID to $vpc_id"
  aws ec2 attach-internet-gateway --internet-gateway-id "$igw_id" --vpc-id "$vpc_id"
}

detach_internet_gateway() {
  local vpc_id
  local igw_id
  vpc_id=$(get_vpc_id)
  igw_id=$(get_internet_gateway_id)
  echo "Detaching internet gateway $igw_id from $vpc_id"
  aws ec2 detach-internet-gateway \
    --internet-gateway-id "$igw_id" \
    --vpc-id "$VPC_ID"
  echo "Detached $igw_id from $vpc_id"
}

delete_internet_gateway() {
  local igw_id
  igw_id=$(get_internet_gateway_id)
  aws ec2 delete-internet-gateway --internet-gateway-id "$igw_id"
  echo "Deleted $igw_id"
}

while getopts "cdt" flag; do
  case "${flag}" in
  t)
    create_vpc
    create_internet_gateway
    attach_internet_gateway
    detach_internet_gateway
    delete_internet_gateway
    delete_vpc
    exit 0
    ;;

  c)
    #    echo "Creating $PROJECT project VPC and its dependencies"
    create_vpc
    create_internet_gateway
    attach_internet_gateway
    exit 0
    ;;
  d)
    #    echo "Deleting $PROJECT project VPC and its dependencies"
    detach_internet_gateway
    delete_internet_gateway
    delete_vpc
    exit 0
    ;;
    #
    #
    #
    #    natgateway_name="public-${NETWORK_BORDER_GROUP}a"
    #    NATGATEWAY_A_ID=$(aws ec2 describe-nat-gateways \
    #      --filter Name=tag:Name,Values=$natgateway_name Name=state,Values=available,pending --query 'NatGateways[].NatGatewayId' \
    #      --output text)
    #    echo "Found $NATGATEWAY_A_ID"
    #
    #    NATGATEWAY_DELETION_MESSAGE=$(aws ec2 delete-nat-gateway --nat-gateway-id "$NATGATEWAY_A_ID")
    #    echo "Deleted $NATGATEWAY_A_ID"
    #
    #    elastic_ip_allocation_name="${NETWORK_BORDER_GROUP}a"
    #    ELASTIC_IP_ALLOCATION_A_ID=$(aws ec2 describe-addresses \
    #      --filter Name=tag:Name,Values=$elastic_ip_allocation_name \
    #      --query Addresses[].AllocationId \
    #      --output text)
    #    echo "Found $ELASTIC_IP_ALLOCATION_A_ID"
    #
    #    aws ec2 release-address --allocation-id "$ELASTIC_IP_ALLOCATION_A_ID"
    #    echo "Released $ELASTIC_IP_ALLOCATION_A_ID"
    #
    #    aws ec2 delete-internet-gateway --internet-gateway-id "$INTERNET_GATEWAY_ID"
    #    echo "Deleted $INTERNET_GATEWAY_ID"

    #    SECURITY_GROUP_ID=$(aws ec2 describe-security-groups \
    #      --filter Name=tag:Name,Values=$PROJECT \
    #      --query SecurityGroups[].GroupId \
    #      --output text)
    #    echo "Found $SECURITY_GROUP_ID"

    #    SECURITY_GROUP_ID=$(aws ec2 describe-security-groups \
    #      --filter Name=tag:Name,Values=$PROJECT \
    #      --query SecurityGroups[].GroupId \
    #      --output text)

    #    aws ec2 delete-security-group --group-id "$SECURITY_GROUP_ID"
    #    echo "Deleted $SECURITY_GROUP_ID"

    #    ROUTE_TABLE_ID=$(aws ec2 describe-route-tables \
    #      --filter Name=vpc-id,Values="$VPC_ID" \
    #      --filter Name=tag:Name,Values=public-$REGION \
    #      --query RouteTables[].RouteTableId \
    #      --output text)
    #    echo "Found $ROUTE_TABLE_ID"
    #
    #    aws ec2 delete-route --route-table-id "$ROUTE_TABLE_ID" --destination-cidr-block 0.0.0.0/0
    #    echo "Deleted routes"
    #
    #    SUBNET_ID=$(aws ec2 describe-subnets \
    #      --filter Name=tag:Name,Values=public-"${AVAILABILITY_ZONES[0]}" \
    #      --query 'Subnets[].SubnetId' \
    #      --output text)
    #    echo "Found $SUBNET_ID"
    #
    #    aws ec2 delete-subnet --subnet-id="$SUBNET_ID"
    #    echo "Deleted $SUBNET_ID"
    #
    #    SUBNET_ID=$(aws ec2 describe-subnets \
    #      --filter Name=tag:Name,Values=public-"${AVAILABILITY_ZONES[1]}" \
    #      --query 'Subnets[].SubnetId' \
    #      --output text)
    #    echo "Found $SUBNET_ID"
    #
    #    aws ec2 delete-subnet --subnet-id="$SUBNET_ID"
    #    echo "Deleted $SUBNET_ID"

    #    SUBNET_ID=$(aws ec2 describe-subnets \
    #      --filter Name=tag:Name,Values=private-"${AVAILABILITY_ZONES[0]}" \
    #      --query 'Subnets[].SubnetId' \
    #      --output text)
    #    echo "Found $SUBNET_ID"
    #
    #    aws ec2 delete-subnet --subnet-id="$SUBNET_ID"
    #    echo "Deleted $SUBNET_ID"

    #    SUBNET_ID=$(aws ec2 describe-subnets \
    #      --filter Name=tag:Name,Values=private-"${AVAILABILITY_ZONES[1]}" \
    #      --query 'Subnets[].SubnetId' \
    #      --output text)
    #    echo "Found $SUBNET_ID"
    #
    #    aws ec2 delete-subnet --subnet-id="$SUBNET_ID"
    #    echo "Deleted $SUBNET_ID"

    #    aws ec2 delete-route-table --route-table-id "$ROUTE_TABLE_ID"
    #    echo "Deleted $ROUTE_TABLE_ID"
    #
    #    aws ec2 delete-vpc --vpc-id "$VPC_ID"
    #    echo "Deleted $VPC_ID"
  \?)
    describe_flags
    ;;
  esac
done

#
#echo "Enabling $VPC_ID DNS hostnames"
#aws ec2 modify-vpc-attribute --vpc-id "$VPC_ID" --enable-dns-hostnames "{\"Value\":true}"
#
#echo "Creating Internet Gateway in $REGION"
#INTERNET_GATEWAY_ID=$(aws ec2 create-internet-gateway \
#  --region $REGION \
#  --tag-specifications "ResourceType=internet-gateway,Tags=[{Key=Name,Value=$PROJECT},{Key=Project,Value=$PROJECT}]" \
#  --query 'InternetGateway.{InternetGatewayId:InternetGatewayId}' \
#  --output text)
#echo "Created $INTERNET_GATEWAY_ID"
#
#echo "Attaching $INTERNET_GATEWAY_ID to $VPC_ID"
#aws ec2 attach-internet-gateway --internet-gateway-id "$INTERNET_GATEWAY_ID" --vpc-id "$VPC_ID"
#
#echo "Creating subnet in $VPC_ID with CIDR block ${SUBNET_CIDR_BLOCKS[0]} and availability zone ${AVAILABILITY_ZONES[0]}"
#SUBNET_PUBLIC_A_ID=$(aws ec2 create-subnet \
#  --vpc-id "$VPC_ID" \
#  --cidr-block "${SUBNET_CIDR_BLOCKS[0]}" \
#  --availability-zone "${AVAILABILITY_ZONES[0]}" \
#  --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=public-${AVAILABILITY_ZONES[0]}}, {Key=kubernetes.io/role/elb,Value=1},{Key=Project,Value=$PROJECT}]" \
#  --query 'Subnet.{SubnetId:SubnetId}' \
#  --output text)
#echo "Created $SUBNET_PUBLIC_A_ID"
#
#echo "Creating subnet in $VPC_ID with CIDR block ${SUBNET_CIDR_BLOCKS[1]} and availability zone ${AVAILABILITY_ZONES[1]}"
#SUBNET_PUBLIC_B_ID=$(aws ec2 create-subnet \
#  --vpc-id "$VPC_ID" \
#  --cidr-block "${SUBNET_CIDR_BLOCKS[1]}" \
#  --availability-zone "${AVAILABILITY_ZONES[1]}" \
#  --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=public-${AVAILABILITY_ZONES[1]}}, {Key=kubernetes.io/role/elb,Value=1},{Key=Project,Value=$PROJECT}]" \
#  --query 'Subnet.{SubnetId:SubnetId}' \
#  --output text)
#echo "Created $SUBNET_PUBLIC_B_ID"

#echo "Creating subnet in $VPC_ID with CIDR block ${SUBNET_CIDR_BLOCKS[2]} and availability zone ${AVAILABILITY_ZONES[0]}"
#SUBNET_PRIVATE_A_ID=$(aws ec2 create-subnet \
#  --vpc-id "$VPC_ID" \
#  --cidr-block "${SUBNET_CIDR_BLOCKS[2]}" \
#  --availability-zone "${AVAILABILITY_ZONES[2]}" \
#  --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=private-${AVAILABILITY_ZONES[0]}}, {Key=kubernetes.io/role/elb,Value=1},{Key=Project,Value=$PROJECT}]" \
#  --query 'Subnet.{SubnetId:SubnetId}' \
#  --output text)
#echo "Created $SUBNET_PRIVATE_A_ID"
#
#echo "Creating subnet in $VPC_ID with CIDR block ${SUBNET_CIDR_BLOCKS[3]} and availability zone ${AVAILABILITY_ZONES[1]}"
#SUBNET_PRIVATE_B_ID=$(aws ec2 create-subnet \
#  --vpc-id "$VPC_ID" \
#  --cidr-block "${SUBNET_CIDR_BLOCKS[3]}" \
#  --availability-zone "${AVAILABILITY_ZONES[3]}" \
#  --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=private-${AVAILABILITY_ZONES[1]}}, {Key=kubernetes.io/role/elb,Value=1},{Key=Project,Value=$PROJECT}]" \
#  --query 'Subnet.{SubnetId:SubnetId}' \
#  --output text)
#echo "Created $SUBNET_PRIVATE_B_ID"

#echo "Enabling $SUBNET_PUBLIC_A_ID auto-assign public IPv4 address"
#aws ec2 modify-subnet-attribute --subnet-id "$SUBNET_PUBLIC_A_ID" --map-public-ip-on-launch "{\"Value\":true}"
#
#echo "Enabling $SUBNET_PUBLIC_B_ID auto-assign public IPv4 address"
#aws ec2 modify-subnet-attribute --subnet-id "$SUBNET_PUBLIC_B_ID" --map-public-ip-on-launch "{\"Value\":true}"
#
#echo "Allocating Elastic IP address for network border group $NETWORK_BORDER_GROUP"
#elastic_ip_allocation_name="${NETWORK_BORDER_GROUP}a"
#ELASTIC_IP_ALLOCATION_A_ID=$(aws ec2 allocate-address \
#  --network-border-group $NETWORK_BORDER_GROUP \
#  --tag-specifications "ResourceType=elastic-ip,Tags=[{Key=Name,Value=$elastic_ip_allocation_name},{Key=Project,Value=$PROJECT}]" \
#  --query '{AllocationId:AllocationId}' \
#  --output text)
#echo "Allocated $ELASTIC_IP_ALLOCATION_A_ID"
#
#echo "Creating NAT gateway in $SUBNET_PUBLIC_A_ID"
#natgateway_name="public-${NETWORK_BORDER_GROUP}a"
#NATGATEWAY_A_ID=$(aws ec2 create-nat-gateway \
#  --subnet-id "$SUBNET_PUBLIC_A_ID" \
#  --allocation-id "$ELASTIC_IP_ALLOCATION_A_ID" \
#  --tag-specifications "ResourceType=natgateway,Tags=[{Key=Name,Value=$natgateway_name},{Key=Project,Value=$PROJECT}]" \
#  --query 'NatGateway.{NatGatewayId:NatGatewayId}' \
#  --output text)
#echo "Created $NATGATEWAY_A_ID"
#
#echo "Creating route table in $VPC_ID"
#ROUTE_TABLE_ID=$(aws ec2 create-route-table --vpc-id "$VPC_ID" \
#  --tag-specifications "ResourceType=route-table,Tags=[{Key=Name,Value=public-$REGION},{Key=Project,Value=$PROJECT}]" \
#  --query 'RouteTable.{RouteTableId:RouteTableId}' \
#  --output text)
#echo "Created route table $ROUTE_TABLE_ID"
#
#echo "Adding route with destination $IPV4_DEFAULT_ROUTE and target $INTERNET_GATEWAY_ID to $ROUTE_TABLE_ID"
#ADDING_ROUTE_RESULT_MESSAGE=$(aws ec2 create-route --route-table-id "$ROUTE_TABLE_ID" \
#  --gateway-id "$INTERNET_GATEWAY_ID" \
#  --destination-cidr-block "0.0.0.0/0")
#
#echo "Associating route table $ROUTE_TABLE_ID with $SUBNET_PUBLIC_A_ID"
#ROUTE_TABLE_ASSOCIATION_A_ID=$(aws ec2 associate-route-table \
#  --route-table-id "$ROUTE_TABLE_ID" \
#  --subnet-id "$SUBNET_PUBLIC_A_ID" \
#  --query "{AssociationId:AssociationId}" \
#  --output text)
#echo "Associated $ROUTE_TABLE_ASSOCIATION_A_ID"
#
#echo "Associating route table $ROUTE_TABLE_ID with $SUBNET_PUBLIC_B_ID"
#ROUTE_TABLE_ASSOCIATION_B_ID=$(aws ec2 associate-route-table \
#  --route-table-id "$ROUTE_TABLE_ID" \
#  --subnet-id "$SUBNET_PUBLIC_B_ID" \
#  --query "{AssociationId:AssociationId}" \
#  --output text)
#echo "Associated $ROUTE_TABLE_ASSOCIATION_B_ID"

#echo "Creating security group in $VPC_ID"
#SECURITY_GROUP_ID=$(
#  aws ec2 create-security-group --group-name $PROJECT \
#    --description "$PROJECT security group" \
#    --vpc-id "$VPC_ID" \
#    --tag-specifications "ResourceType=security-group,Tags=[{Key=Name,Value=$PROJECT},{Key=Project,Value=$PROJECT}]" \
#    --query "{GroupId:GroupId}" \
#    --output text
#)
#echo "Created security group $SECURITY_GROUP_ID"
#
#echo "Adding rule to $SECURITY_GROUP_ID"
#SECURITY_GROUP_ADD_RESULT_MESSAGE=$(aws ec2 authorize-security-group-ingress --group-id "$SECURITY_GROUP_ID" \
#  --protocol tcp --port 22 --cidr 0.0.0.0/0)
#
#echo "Adding rule to $SECURITY_GROUP_ID"
#SECURITY_GROUP_ADD_RESULT_MESSAGE=$(aws ec2 authorize-security-group-ingress --group-id "$SECURITY_GROUP_ID" \
#  --protocol tcp --port 80 --cidr 0.0.0.0/0)
#
#echo "Adding rule to $SECURITY_GROUP_ID"
#SECURITY_GROUP_ADD_RESULT_MESSAGE=$(aws ec2 authorize-security-group-ingress --group-id "$SECURITY_GROUP_ID" \
#  --protocol tcp --port 443 --cidr 0.0.0.0/0)
#
#echo "Adding rule to $SECURITY_GROUP_ID"
#SECURITY_GROUP_ADD_RESULT_MESSAGE=$(aws ec2 authorize-security-group-ingress --group-id "$SECURITY_GROUP_ID" \
#  --protocol icmp --port all --cidr 0.0.0.0/0)
