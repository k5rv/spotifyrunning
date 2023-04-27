#!/bin/bash
#
# Creates AWS resources: VPC, internet gateway, subnets, route tables, security group

PROJECT="spotifyrun"
REGION="eu-north-1"
SUBNET_CIDR_BLOCKS=("10.0.1.0/24" "10.0.2.0/24" "10.0.3.0/24" "10.0.4.0/24")

describe_flags() {
  echo "Available flags:"
  echo "-d: delete $PROJECT project VPC and its dependencies"
  echo "-c: create $PROJECT project VPC and its dependencies"
}

create_vpc() {
  local project="$1"
  local region="$2"
  local cidr_block="$3"
  local vpc_id

  vpc_id=$(aws ec2 create-vpc \
    --cidr-block "$cidr_block" \
    --region "$region" \
    --tag-specification "ResourceType=vpc,Tags=[{Key=Name,Value=$project},{Key=Project,Value=$project},{Key=kubernetes.io/cluster/$project,Value=owned}]" \
    --query "Vpc.{VpcId:VpcId}" \
    --output text)
  echo "$vpc_id"
}

enable_dns_hostname() {
  local vpc_id="$1"
  aws ec2 modify-vpc-attribute --vpc-id "$vpc_id" --enable-dns-hostnames "{\"Value\":true}"
}

get_vpc_id() {
  local vpc_name=$1
  vpc_id=$(aws ec2 describe-vpcs --filter Name=tag:Name,Values="$vpc_name" --query "Vpcs[].VpcId" --output text)
  echo "$vpc_id"
}

delete_vpc() {
  local vpc_id="$1"
  aws ec2 delete-vpc --vpc-id "$vpc_id"
}

create_internet_gateway() {
  project="$1"
  region="$2"

  igw_id=$(aws ec2 create-internet-gateway \
    --region "$region" \
    --tag-specifications "ResourceType=internet-gateway,Tags=[{Key=Name,Value=$project},{Key=Project,Value=$project}]" \
    --query "InternetGateway.{InternetGatewayId:InternetGatewayId}" \
    --output text)
  echo "$igw_id"
}

get_internet_gateway_id() {
  local project="$1"
  igw_id=$(aws ec2 describe-internet-gateways \
    --filter "Name=tag:Name,Values=$project" \
    --query "InternetGateways[].InternetGatewayId" \
    --output text)
  echo "$igw_id"
}

attach_internet_gateway() {
  local vpc_id="$1"
  local igw_id="$2"
  aws ec2 attach-internet-gateway --internet-gateway-id "$igw_id" --vpc-id "$vpc_id"
}

detach_internet_gateway() {
  local vpc_id="$1"
  local igw_id="$2"
  aws ec2 detach-internet-gateway --internet-gateway-id "$igw_id" --vpc-id "$vpc_id"
}

delete_internet_gateway() {
  local igw_id="$1"
  aws ec2 delete-internet-gateway --internet-gateway-id "$igw_id"
}

allocate_eip() {
  local project="$1"
  local network_border_group="$2"
  local eip_name="$3"
  eip_id=$(aws ec2 allocate-address \
    --network-border-group "$network_border_group" \
    --tag-specifications "ResourceType=elastic-ip,Tags=[{Key=Name,Value=$eip_name},{Key=Project,Value=$project}]" \
    --query '{AllocationId:AllocationId}' \
    --output text)
  echo "$eip_id"
}

get_eip_id() {
  local eip_name="$1"
  eip_id=$(aws ec2 describe-addresses \
    --filter "Name=tag:Name,Values=$eip_name" \
    --query Addresses[].AllocationId \
    --output text)
  echo "$eip_id"
}

release_eip_id() {
  local eip_id=$1
  aws ec2 release-address --allocation-id "$eip_id"
}

create_subnet() {
  local project="$1"
  local vpc_id="$2"
  local availability_zone="$3"
  local cidr_block="$4"
  local subnet_name="$5"

  subnet_id=$(aws ec2 create-subnet \
    --vpc-id "$vpc_id" \
    --cidr-block "$cidr_block" \
    --availability-zone "$availability_zone" \
    --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=$subnet_name}, {Key=kubernetes.io/role/elb,Value=1},{Key=Project,Value=$project}]" \
    --query 'Subnet.{SubnetId:SubnetId}' \
    --output text)
  echo "$subnet_id"
}

get_subnet_id() {
  local subnet_name="$1"
  subnet_id=$(aws ec2 describe-subnets \
    --filter "Name=tag:Name,Values=$subnet_name" \
    --query 'Subnets[].SubnetId' \
    --output text)
  echo "$subnet_id"
}

enable_subnet_public_ipv4_address_auto_assign() {
  local subnet_id="$1"
  aws ec2 modify-subnet-attribute --subnet-id "$subnet_id" --map-public-ip-on-launch "{\"Value\":true}"
}

delete_subnet() {
  local subnet_id="$1"
  aws ec2 delete-subnet --subnet-id="$subnet_id"
}

create_route_table() {
  local project="$1"
  local vpc_id="$2"
  local route_table_name="$3"
  route_table_id=$(aws ec2 create-route-table --vpc-id "$vpc_id" \
    --tag-specifications "ResourceType=route-table,Tags=[{Key=Name,Value=$route_table_name},{Key=Project,Value=$project}]" \
    --query 'RouteTable.{RouteTableId:RouteTableId}' \
    --output text)
  echo "$route_table_id"
}

get_route_table_id() {
  local vpc_id="$1"
  local route_table_name="$2"
  route_table_id=$(aws ec2 describe-route-tables \
    --filter "Name=vpc-id,Values=$vpc_id" "Name=tag:Name,Values=$route_table_name" \
    --query RouteTables[].RouteTableId \
    --output text)
  echo "$route_table_id"
}

delete_route_table() {
  local route_table_id="$1"
  aws ec2 delete-route-table --route-table-id "$route_table_id"
}

add_route() {
  local route_table_id="$1"
  local target_id="$2"
  local destination_cidr_block="$3"
  aws ec2 create-route --route-table-id "$route_table_id" \
    --gateway-id "$target_id" \
    --destination-cidr-block "$destination_cidr_block" >/dev/null
}

delete_route() {
  local route_table_id="$1"
  local destination_cidr_block="$2"
  aws ec2 delete-route --route-table-id "$route_table_id" --destination-cidr-block "$destination_cidr_block"
}

associate_route_table() {
  local route_table_id="$1"
  local subnet_id="$2"
  association_id=$(aws ec2 associate-route-table \
    --route-table-id "$route_table_id" \
    --subnet-id "$subnet_id" \
    --query "{AssociationId:AssociationId}" \
    --output text)
  echo "$association_id"
}

get_route_table_association_id() {
  local route_table_id="$1"
  local subnet_id="$2"
  association_id=$(
    aws ec2 describe-route-tables \
      --filter "Name=association.route-table-id,Values=$route_table_id" "Name=association.subnet-id,Values=$subnet_id" \
      --query "RouteTables[].Associations[].RouteTableAssociationId" \
      --output text
  )
  echo "$association_id"
}

delete_route_table_association() {
  local route_table_association_id="$1"
  aws ec2 disassociate-route-table --association-id "$route_table_association_id"
}

create_natgateway() {
  local project="$1"
  local natgateway_name="$2"
  local subnet_id="$3"
  local eip_id="$4"

  natgateway_id=$(aws ec2 create-nat-gateway \
    --connectivity-type "public" \
    --subnet-id "$subnet_id" \
    --allocation-id "$eip_id" \
    --tag-specifications "ResourceType=natgateway,Tags=[{Key=Name,Value=$natgateway_name},{Key=Project,Value=$project}]" \
    --query 'NatGateway.{NatGatewayId:NatGatewayId}' \
    --output text)

  echo "$natgateway_id"
}

get_natgateway_id() {
  local natgateway_name="$1"
  local subnet_id="$2"

  natgateway_id=$(aws ec2 describe-nat-gateways \
    --filter "Name=tag:Name,Values=$natgateway_name" "Name=subnet-id,Values=$subnet_id" "Name=state,Values=available,pending" \
    --query "NatGateways[].NatGatewayId" \
    --output text)

  echo "$natgateway_id"
}

get_natgateway_state() {
  local natgateway_name="$1"
  local subnet_id="$2"

  natgateway_state=$(aws ec2 describe-nat-gateways \
    --filter "Name=tag:Name,Values=$natgateway_name" "Name=subnet-id,Values=$subnet_id" \
    --query "NatGateways[].State" \
    --output text)

  echo "$natgateway_state"
}

delete_natgateway() {
  local natgateway_id="$1"
  aws ec2 delete-nat-gateway --nat-gateway-id "$natgateway_id" >/dev/null
}

get_network_interface_id() {
  local subnet_id="$1"
  eni_id=$(aws ec2 describe-network-interfaces --filter "Name=subnet-id,Values=$subnet_id" \
    --query "NetworkInterfaces[].NetworkInterfaceId" \
    --output text)
  echo "$eni_id"
}

#get_network_interface_association_id() {
#  local subnet_id="$1"
#  attachment_id=$(aws ec2 describe-network-interfaces)
#  #  --filter "Name=subnet-id,Values=$subnet_id" \
#  #    --query "NetworkInterfaces[].Association[].AssociationId" \
#  #    --output text)
#  echo "$attachment_id"
#}

#get_network_interface_attachmen_id() {
#  local subnet_id="$1"
#  attachment_id=$(aws ec2 describe-network-interfaces --filter "Name=subnet-id,Values=$subnet_id" \
#    --query "NetworkInterfaces[].Attachment[].AttachmentId" \
#    --output text)
#  echo "$attachment_id"
#}

#get_network_interface_public_ip() {
#  local subnet_id="$1"
#  attachment_id=$(aws ec2 describe-network-interfaces --filter "Name=subnet-id,Values=$subnet_id" \
#    --query "NetworkInterfaces[].Attachment[].PublicIp" \
#    --output text)
#  echo "$attachment_id"
#}

#get_address_public_ip() {
#  local eip_id="$1"
#  association_id=$(aws ec2 describe-addresses --allocation-ids "$eip_id" \
#    --query "Addresses[].AssociationId" \
#    --output text)
#  echo "$association_id"
#}

# "Addresses": [
#        {
#            "PublicIp": "13.51.153.24",
#            "AllocationId": "eipalloc-0ffd0b5e294354d6d",
#            "AssociationId": "eipassoc-01008a93ae611cff8",

#aws ec2 describe-addresses \
#    --filters "Name=domain,Values=standard"

#get_address_association_id() {
#  local eip_id="$1"
#  association_id=$(aws ec2 describe-addresses --allocation-ids "$eip_id" \
#    --query "Addresses[].AssociationId" \
#    --output text)
#  echo "$association_id"
#}
#
#disassociate_address() {
#  local association_id="$1"
#  aws ec2 disassociate-address --association-id "$association_id"
#}

#    eip_a_name="eu-north-1a"
#    eip_a_id=$(get_eip_id "$eip_a_name")
#    address_association_id=$(get_address_association_id "$eip_a_id")
#    disassociate_address "$address_association_id"
#
#
#detach_network_interface() {
#  local eni_id="$1"
#  aws ec2 detach-network-interface --attachment-id "$eni_id"
#}
#PublicIp": "203.0.113.12",
# get network interface ip address
#aws ec2 disassociate-address --public-ip 198.51.100.0

while getopts "tcd" flag; do
  case "${flag}" in
  t)
    subnet_name="public-eu-north-1a"
    subnet_id=$(get_subnet_id $subnet_name)
    natgateway_name="public-eu-north-1a"
    natgateway_state=$(get_natgateway_state $natgateway_name "$subnet_id")
    while [ "$eni_id" != 'deleted' ]; do
      natgateway_state=$(get_natgateway_state $natgateway_name "$subnet_id")
      echo "$natgateway_state"
      sleep 1
    done
    echo "It's gone"
    ;;
  c)
    project=$PROJECT
    region=$REGION

    vpc_cidr_block="10.0.0.0/16"
    vpc_id=$(create_vpc $project $region $vpc_cidr_block)
    echo "Created $vpc_id"

    enable_dns_hostname "$vpc_id"
    echo "Enabled DNS hostnames for $vpc_id"

    igw_id=$(create_internet_gateway $project $region)
    echo "Created $igw_id"

    attach_internet_gateway "$vpc_id" "$igw_id"
    echo "Attached $igw_id to $vpc_id"

    eip_network_border_group="eu-north-1"
    eip_a_name="eu-north-1a"
    eip_a_id=$(allocate_eip $project $eip_network_border_group $eip_a_name)
    echo "Allocated $eip_a_id"

    subnet_public_a_availability_zone="eu-north-1a"
    subnet_public_a_cidr_block="10.0.1.0/24"
    subnet_public_a_name="public-eu-north-1a"
    subnet_public_a_id=$(create_subnet $project "$vpc_id" $subnet_public_a_availability_zone $subnet_public_a_cidr_block $subnet_public_a_name)
    echo "Created $subnet_public_a_id"

    enable_subnet_public_ipv4_address_auto_assign "$subnet_public_a_id"
    echo "Enabled public IPv4 address auto-assigning for $subnet_public_a_id"

    route_table_public_name="public-eu-north-1"
    route_table_public_id=$(create_route_table $project "$vpc_id" $route_table_public_name)
    echo "Created $route_table_public_id"

    route_destination_cidr_block="0.0.0.0/0"
    add_route "$route_table_public_id" "$igw_id" $route_destination_cidr_block
    echo "Added route to $route_table_public_id with target $igw_id and destination CIDR block $route_destination_cidr_block"

    route_table_association_public_a_id=$(associate_route_table "$route_table_public_id" "$subnet_public_a_id")
    echo "Associated $route_table_public_id and $subnet_public_a_id in $route_table_association_public_a_id"

    natgateway_public_a_name="public-eu-north-1a"
    natgateway_public_a_id=$(create_natgateway $project $natgateway_public_a_name "$subnet_public_a_id" "$eip_a_id")
    echo "Created $natgateway_public_a_id"
    ;;
  d)
    project="spotifyrun"
    region="eu-north-1"
    vpc_name=$project

    vpc_id=$(get_vpc_id $vpc_name)

    route_table_public_name="public-eu-north-1"
    route_table_public_id=$(get_route_table_id "$vpc_id" $route_table_public_name)
    route_destination_cidr_block="0.0.0.0/0"
    delete_route "$route_table_public_id" $route_destination_cidr_block
    echo "Deleted $route_table_public_id route with destination $route_destination_cidr_block"

    subnet_public_a_name="public-eu-north-1a"
    subnet_public_a_id=$(get_subnet_id $subnet_public_a_name)
    route_table_association_public_a_id=$(get_route_table_association_id "$route_table_public_id" "$subnet_public_a_id")
    delete_route_table_association "$route_table_association_public_a_id"
    echo "Deleted $route_table_association_public_a_id"

    delete_route_table "$route_table_public_id"
    echo "Deleted $route_table_public_id"

    natgateway_public_a_name="public-eu-north-1a"
    natgateway_public_a_id=$(get_natgateway_id "$natgateway_public_a_name" "$subnet_public_a_id")
    delete_natgateway "$natgateway_public_a_id"
    echo "Deleted $natgateway_public_a_id"

    subnet_name="public-eu-north-1a"
    subnet_id=$(get_subnet_id $subnet_name)
    natgateway_name="public-eu-north-1a"
    natgateway_state=$(get_natgateway_state $natgateway_name "$subnet_id")
    while [ "$natgateway_state" != 'deleted' ]; do
      natgateway_state=$(get_natgateway_state $natgateway_name "$subnet_id")
      echo "$natgateway_state"
      sleep 1
    done
    echo "It's gone"

    igw_id=$(get_internet_gateway_id $project)
    detach_internet_gateway "$vpc_id" "$igw_id"
    echo "Detached $igw_id from $vpc_id"

    delete_internet_gateway "$igw_id"
    echo "Deleted $igw_id"

    eip_a_name="eu-north-1a"
    eip_a_id=$(get_eip_id $eip_a_name)
    release_eip_id "$eip_a_id"
    echo "Released $eip_a_id"

    delete_subnet "$subnet_public_a_id"
    echo "Deleted $subnet_public_a_id"

    delete_vpc "$vpc_id"
    echo "Deleted $vpc_id"
    ;;

    #    natgateway_name="public-${NETWORK_BORDER_GROUP}a"
    #    NATGATEWAY_A_ID=$(aws ec2 describe-nat-gateways \
    #      --filter Name=tag:Name,Values=$natgateway_name Name=state,Values=available,pending --query 'NatGateways[].NatGatewayId' \
    #      --output text)
    #    echo "Found $NATGATEWAY_A_ID"
    #
    #    NATGATEWAY_DELETION_MESSAGE=$(aws ec2 delete-nat-gateway --nat-gateway-id "$NATGATEWAY_A_ID")
    #    echo "Deleted $NATGATEWAY_A_ID"
    #

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

  \?)
    describe_flags
    ;;
  esac
done

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
