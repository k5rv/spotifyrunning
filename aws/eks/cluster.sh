#!/bin/bash
# Creates aws resources for eks cluster: vpc, internet gateway, subnets, route tables, security group

PROJECT="spotifyrun"
REGION="eu-north-1"
VPC_CIDR_BLOCK="10.0.0.0/16"
AVAILABILITY_ZONES=("eu-north-1a" "eu-north-1b")
SUBNET_CIDR_BLOCKS=("10.0.1.0/24" "10.0.2.0/24" "10.0.3.0/24" "10.0.4.0/24")

#CHECK_FREQUENCY=5

describe_flags() {
  echo "Available flags:"
  echo "-d: delete $PROJECT cluster"
}

while getopts 'd' flag; do
  case "${flag}" in
  d)
    echo "Looking for VPC in project $PROJECT"
    VPC_ID=$(aws ec2 describe-vpcs \
      --filter Name=tag:Name,Values=$PROJECT \
      --query Vpcs[].VpcId \
      --output text)
    echo "Found $VPC_ID"

    echo "Looking for internet gateway in project $PROJECT"
    INTERNET_GATEWAY_ID=$(aws ec2 describe-internet-gateways \
      --filter Name=tag:Name,Values=$PROJECT \
      --query InternetGateways[].InternetGatewayId \
      --output text)
    echo "Found $INTERNET_GATEWAY_ID"

    echo "Detaching $INTERNET_GATEWAY_ID from $VPC_ID"
    aws ec2 detach-internet-gateway \
      --internet-gateway-id "$INTERNET_GATEWAY_ID" \
      --vpc-id "$VPC_ID"
    echo "Detached $INTERNET_GATEWAY_ID from $VPC_ID"

    echo "Deleting $INTERNET_GATEWAY_ID"
    aws ec2 delete-internet-gateway --internet-gateway-id "$INTERNET_GATEWAY_ID"
    echo "Deleted $INTERNET_GATEWAY_ID"

    echo "Looking for security group in project $PROJECT"
    SECURITY_GROUP_ID=$(aws ec2 describe-security-groups \
      --filter Name=tag:Name,Values=$PROJECT \
      --query SecurityGroups[].GroupId \
      --output text)
    echo "Found $SECURITY_GROUP_ID"

    echo "Deleting $SECURITY_GROUP_ID"
    aws ec2 delete-security-group --group-id "$SECURITY_GROUP_ID"
    echo "Deleted $SECURITY_GROUP_ID"

    SUBNET_ID=$(aws ec2 describe-subnets \
      --filter Name=tag:Name,Values=public-"${AVAILABILITY_ZONES[0]}" \
      --query 'Subnets[].SubnetId' \
      --output text)

    aws ec2 delete-subnet --subnet-id="$SUBNET_ID"
    echo "Deleted $SUBNET_ID"

    SUBNET_ID=$(aws ec2 describe-subnets \
      --filter Name=tag:Name,Values=public-"${AVAILABILITY_ZONES[1]}" \
      --query 'Subnets[].SubnetId' \
      --output text)

    aws ec2 delete-subnet --subnet-id="$SUBNET_ID"
    echo "Deleted $SUBNET_ID"

    SUBNET_ID=$(aws ec2 describe-subnets \
      --filter Name=tag:Name,Values=private-"${AVAILABILITY_ZONES[0]}" \
      --query 'Subnets[].SubnetId' \
      --output text)

    aws ec2 delete-subnet --subnet-id="$SUBNET_ID"
    echo "Deleted $SUBNET_ID"

    SUBNET_ID=$(aws ec2 describe-subnets \
      --filter Name=tag:Name,Values=private-"${AVAILABILITY_ZONES[1]}" \
      --query 'Subnets[].SubnetId' \
      --output text)

    aws ec2 delete-subnet --subnet-id="$SUBNET_ID"
    echo "Deleted $SUBNET_ID"

    echo "Looking for subnets in $VPC_ID"
    ROUTE_TABLE_ID=$(aws ec2 describe-route-tables \
      --filter Name=vpc-id,Values="$VPC_ID" \
      --filter Name=tag:Name,Values=public-$REGION \
      --query RouteTables[].RouteTableId \
      --output text)
    echo "Found $ROUTE_TABLE_ID"

    echo "Looking for route table in $VPC_ID"
    ROUTE_TABLE_ID=$(aws ec2 describe-route-tables \
      --filter Name=vpc-id,Values="$VPC_ID" \
      --filter Name=tag:Name,Values=public-$REGION \
      --query RouteTables[].RouteTableId \
      --output text)
    echo "Found $ROUTE_TABLE_ID"

    echo "Deleting $ROUTE_TABLE_ID"
    aws ec2 delete-route-table --route-table-id "$ROUTE_TABLE_ID"
    echo "Deleted $ROUTE_TABLE_ID"

    echo "Deleting $VPC_ID"
    aws ec2 delete-vpc --vpc-id "$VPC_ID"
    echo "Deleted $VPC_ID"

    exit 0
    ;;
  *)
    describe_flags
    exit 0
    ;;
  esac
done

# create VPC
echo "Creating VPC in $REGION"
VPC_ID=$(aws ec2 create-vpc \
  --cidr-block $VPC_CIDR_BLOCK \
  --region $REGION \
  --tag-specification "ResourceType=vpc,Tags=[{Key=Name,Value=$PROJECT}, {Key=kubernetes.io/cluster/$PROJECT,Value=owned}]" \
  --query 'Vpc.{VpcId:VpcId}' \
  --output text)
echo "Created $VPC_ID"

# modify VPC: enable DNS hostnames
echo "Enabling $VPC_ID DNS hostnames"
aws ec2 modify-vpc-attribute --vpc-id "$VPC_ID" --enable-dns-hostnames "{\"Value\":true}"
echo "Enabled $VPC_ID DNS hostnames"

# create Internet Gateway
echo "Creating Internet Gateway in $REGION"
INTERNET_GATEWAY_ID=$(aws ec2 create-internet-gateway \
  --region $REGION \
  --tag-specifications "ResourceType=internet-gateway,Tags=[{Key=Name,Value=$PROJECT}]" \
  --query 'InternetGateway.{InternetGatewayId:InternetGatewayId}' \
  --output text)
echo "Created $INTERNET_GATEWAY_ID"

# attach Internet Gateway
echo "Attaching $INTERNET_GATEWAY_ID to $VPC_ID"
aws ec2 attach-internet-gateway --internet-gateway-id "$INTERNET_GATEWAY_ID" --vpc-id "$VPC_ID"
echo "Attached $INTERNET_GATEWAY_ID to $VPC_ID"

# create subnets
echo "Creating subnet in $VPC_ID with CIDR block ${SUBNET_CIDR_BLOCKS[0]} and availability zone ${AVAILABILITY_ZONES[0]}"
SUBNET_PUBLIC_A_ID=$(aws ec2 create-subnet \
  --vpc-id "$VPC_ID" \
  --cidr-block "${SUBNET_CIDR_BLOCKS[0]}" \
  --availability-zone "${AVAILABILITY_ZONES[0]}" \
  --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=public-${AVAILABILITY_ZONES[0]}}, {Key=kubernetes.io/role/elb,Value=1}]" \
  --query 'Subnet.{SubnetId:SubnetId}' \
  --output text)
echo "Created $SUBNET_PUBLIC_A_ID"

echo "Creating subnet in $VPC_ID with CIDR block ${SUBNET_CIDR_BLOCKS[1]} and availability zone ${AVAILABILITY_ZONES[1]}"
SUBNET_PUBLIC_B_ID=$(aws ec2 create-subnet \
  --vpc-id "$VPC_ID" \
  --cidr-block "${SUBNET_CIDR_BLOCKS[1]}" \
  --availability-zone "${AVAILABILITY_ZONES[1]}" \
  --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=public-${AVAILABILITY_ZONES[1]}}, {Key=kubernetes.io/role/elb,Value=1}]" \
  --query 'Subnet.{SubnetId:SubnetId}' \
  --output text)
echo "Created $SUBNET_PUBLIC_B_ID"

echo "Creating subnet in $VPC_ID with CIDR block ${SUBNET_CIDR_BLOCKS[2]} and availability zone ${AVAILABILITY_ZONES[0]}"
SUBNET_PRIVATE_A_ID=$(aws ec2 create-subnet \
  --vpc-id "$VPC_ID" \
  --cidr-block "${SUBNET_CIDR_BLOCKS[2]}" \
  --availability-zone "${AVAILABILITY_ZONES[2]}" \
  --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=private-${AVAILABILITY_ZONES[0]}}, {Key=kubernetes.io/role/elb,Value=1}]" \
  --query 'Subnet.{SubnetId:SubnetId}' \
  --output text)
echo "Created $SUBNET_PRIVATE_A_ID"

echo "Creating subnet in $VPC_ID with CIDR block ${SUBNET_CIDR_BLOCKS[3]} and availability zone ${AVAILABILITY_ZONES[1]}"
SUBNET_PRIVATE_B_ID=$(aws ec2 create-subnet \
  --vpc-id "$VPC_ID" \
  --cidr-block "${SUBNET_CIDR_BLOCKS[3]}" \
  --availability-zone "${AVAILABILITY_ZONES[3]}" \
  --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=private-${AVAILABILITY_ZONES[1]}}, {Key=kubernetes.io/role/elb,Value=1}]" \
  --query 'Subnet.{SubnetId:SubnetId}' \
  --output text)
echo "Created $SUBNET_PRIVATE_B_ID"

# modify subnets: enable auto-assign public IPv4 address
echo "Enabling $SUBNET_PUBLIC_A_ID auto-assign public IPv4 address"
aws ec2 modify-subnet-attribute --subnet-id "$SUBNET_PUBLIC_A_ID" --map-public-ip-on-launch "{\"Value\":true}"
echo "Enabled $SUBNET_PUBLIC_A_ID auto-assign public IPv4 address"

echo "Enabling $SUBNET_PUBLIC_B_ID auto-assign public IPv4 address"
aws ec2 modify-subnet-attribute --subnet-id "$SUBNET_PUBLIC_B_ID" --map-public-ip-on-launch "{\"Value\":true}"
echo "Enabled $SUBNET_PUBLIC_B_ID auto-assign public IPv4 address"

# create route table
echo "Creating route table in $VPC_ID"
ROUTE_TABLE_ID=$(aws ec2 create-route-table --vpc-id "$VPC_ID" \
  --tag-specifications "ResourceType=route-table,Tags=[{Key=Name,Value=public-$REGION}]" \
  --query 'RouteTable.{RouteTableId:RouteTableId}' \
  --output text)
echo "Created route table $ROUTE_TABLE_ID"

# modify route table: add route
echo "Adding route with destination $IPV4_DEFAULT_ROUTE and target $INTERNET_GATEWAY_ID to $ROUTE_TABLE_ID"
ADDING_ROUTE_RESULT_MESSAGE=$(aws ec2 create-route --route-table-id "$ROUTE_TABLE_ID" \
  --gateway-id "$INTERNET_GATEWAY_ID" \
  --destination-cidr-block "0.0.0.0/0")
echo "Added route"

# associate route table: with subnets
echo "Associating route table $ROUTE_TABLE_ID with $SUBNET_PUBLIC_A_ID"
ROUTE_TABLE_ASSOCIATION_RESULT_MESSAGE=$(aws ec2 associate-route-table --route-table-id "$ROUTE_TABLE_ID" --subnet-id "$SUBNET_PUBLIC_A_ID")
echo "Associated route table"

echo "Associating route table $ROUTE_TABLE_ID with $SUBNET_PUBLIC_B_ID"
ROUTE_TABLE_ASSOCIATION_RESULT_MESSAGE=$(aws ec2 associate-route-table --route-table-id "$ROUTE_TABLE_ID" --subnet-id "$SUBNET_PUBLIC_B_ID")
echo "Associated route table"

# create security group
echo "Creating security group in $VPC_ID"
SECURITY_GROUP_ID=$(
  aws ec2 create-security-group --group-name $PROJECT \
    --description "$PROJECT security group" \
    --vpc-id "$VPC_ID" \
    --tag-specifications "ResourceType=security-group,Tags=[{Key=Name,Value=$PROJECT}]" \
    --query "{GroupId:GroupId}" \
    --output text
)
echo "Created security group $SECURITY_GROUP_ID"

# modify security group: add rules
echo "Adding rule to $SECURITY_GROUP_ID"
SECURITY_GROUP_ADD_RESULT_MESSAGE=$(aws ec2 authorize-security-group-ingress --group-id "$SECURITY_GROUP_ID" \
  --protocol tcp --port 80 --cidr 0.0.0.0/0)
echo "Added rule to $SECURITY_GROUP_ID"

echo "Adding rule to $SECURITY_GROUP_ID"
SECURITY_GROUP_ADD_RESULT_MESSAGE=$(aws ec2 authorize-security-group-ingress --group-id "$SECURITY_GROUP_ID" \
  --protocol tcp --port 443 --cidr 0.0.0.0/0)
echo "Added rule to $SECURITY_GROUP_ID"

echo "Adding rule to $SECURITY_GROUP_ID"
SECURITY_GROUP_ADD_RESULT_MESSAGE=$(aws ec2 authorize-security-group-ingress --group-id "$SECURITY_GROUP_ID" \
  --protocol icmp --port all --cidr 0.0.0.0/0)
echo "Added rule to $SECURITY_GROUP_ID"
