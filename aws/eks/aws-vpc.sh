#!/bin/bash
#
# Creates AWS resources: VPC, internet gateway, public and private subnets, natgateway, route tables, security group

describe_flags() {
  echo "Available flags:"
  echo "-d: delete VPC and its dependencies"
  echo "-c: create VPC and its dependencies"
}

create_vpc() {
  local vpc_name="$1"
  local cluster_name="$2"
  local region="$3"
  local cidr_block="$4"
  vpc_id=$(aws ec2 create-vpc \
    --cidr-block "$cidr_block" \
    --region "$region" \
    --tag-specification "ResourceType=vpc,Tags=[{Key=Name,Value=$vpc_name},{Key=kubernetes.io/cluster/$cluster_name,Value=owned}]" \
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
  igw_name="$1"
  region="$2"
  igw_id=$(aws ec2 create-internet-gateway \
    --region "$region" \
    --tag-specifications "ResourceType=internet-gateway,Tags=[{Key=Name,Value=$igw_name}]" \
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

delete_internet_gateway() {
  local igw_id="$1"
  aws ec2 delete-internet-gateway --internet-gateway-id "$igw_id"
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

allocate_eip() {
  local eip_name="$1"
  local network_border_group="$2"
  eip_id=$(aws ec2 allocate-address \
    --network-border-group "$network_border_group" \
    --tag-specifications "ResourceType=elastic-ip,Tags=[{Key=Name,Value=$eip_name}]" \
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
  subnet_name="$1"
  cidr="$2"
  availability_zone="$3"
  vpc_id="$4"
  elb_tag="$5"
  subnet_id=$(aws ec2 create-subnet \
    --vpc-id "$vpc_id" \
    --cidr-block "$cidr" \
    --availability-zone "$availability_zone" \
    --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=$subnet_name}, $elb_tag]" \
    --query "Subnet.{SubnetId:SubnetId}" \
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

delete_subnet() {
  local subnet_id="$1"
  aws ec2 delete-subnet --subnet-id="$subnet_id"
}

enable_subnet_public_ipv4_address_auto_assign() {
  local subnet_id="$1"
  aws ec2 modify-subnet-attribute --subnet-id "$subnet_id" --map-public-ip-on-launch "{\"Value\":true}"
}

create_route_table() {
  local route_table_name="$1"
  local vpc_id="$2"
  route_table_id=$(aws ec2 create-route-table --vpc-id "$vpc_id" \
    --tag-specifications "ResourceType=route-table,Tags=[{Key=Name,Value=$route_table_name}]" \
    --query "RouteTable.{RouteTableId:RouteTableId}" \
    --output text)
  echo "$route_table_id"
}

get_route_table_id() {
  local vpc_id="$1"
  local route_table_name="$2"
  route_table_id=$(aws ec2 describe-route-tables \
    --filter "Name=vpc-id,Values=$vpc_id" "Name=tag:Name,Values=$route_table_name" \
    --query "RouteTables[].RouteTableId" \
    --output text)
  echo "$route_table_id"
}

delete_route_table() {
  local route_table_id="$1"
  aws ec2 delete-route-table --route-table-id "$route_table_id"
}

create_internet_gateway_route() {
  local route_table_id="$1"
  local igw_id="$2"
  local destination_cidr_block="$3"
  aws ec2 create-route --route-table-id "$route_table_id" \
    --gateway-id "$igw_id" \
    --destination-cidr-block "$destination_cidr_block" >/dev/null
}

create_natgateway_route() {
  local route_table_id="$1"
  local ngw_id="$2"
  local destination_cidr_block="$3"
  aws ec2 create-route --route-table-id "$route_table_id" \
    --nat-gateway-id "$ngw_id" \
    --destination-cidr-block "$destination_cidr_block" >/dev/null
}

delete_route() {
  local route_table_id="$1"
  local destination_cidr_block="$2"
  aws ec2 delete-route --route-table-id "$route_table_id" --destination-cidr-block "$destination_cidr_block"
}

create_route_table_association() {
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
  association_id=$(aws ec2 describe-route-tables \
    --filter "Name=association.route-table-id,Values=$route_table_id" "Name=association.subnet-id,Values=$subnet_id" \
    --query "RouteTables[].Associations[].RouteTableAssociationId" \
    --output text)
  echo "$association_id"
}

delete_route_table_association() {
  local route_table_association_id="$1"
  aws ec2 disassociate-route-table --association-id "$route_table_association_id"
}

poll_natgateway_state() {
  local natgateway_id="$1"
  local state="$2"
  echo "Waiting for NGW $natgateway_id to become '$state' current state is '$(get_natgateway_state "$natgateway_id")'"
  while [ "$natgateway_state" != "$state" ]; do
    echo "..."
    natgateway_state=$(get_natgateway_state "$natgateway_id")
    sleep 5
  done
}

create_natgateway() {
  local natgateway_name="$1"
  local subnet_id="$2"
  local eip_id="$3"
  natgateway_id=$(aws ec2 create-nat-gateway \
    --connectivity-type "public" \
    --subnet-id "$subnet_id" \
    --allocation-id "$eip_id" \
    --tag-specifications "ResourceType=natgateway,Tags=[{Key=Name,Value=$natgateway_name}]" \
    --query "NatGateway.{NatGatewayId:NatGatewayId}" \
    --output text)
  echo "$natgateway_id"
}

get_natgateway_id() {
  local natgateway_name="$1"
  natgateway_id=$(aws ec2 describe-nat-gateways \
    --filter "Name=tag:Name,Values=$natgateway_name" "Name=state,Values=available,pending" \
    --query "NatGateways[].NatGatewayId" \
    --output text)
  echo "$natgateway_id"
}

get_natgateway_state() {
  local natgateway_id="$1"
  natgateway_state=$(aws ec2 describe-nat-gateways \
    --filter "Name=nat-gateway-id,Values=$natgateway_id" \
    --query "NatGateways[].State" \
    --output text)
  echo "$natgateway_state"
}

delete_natgateway() {
  local natgateway_id="$1"
  aws ec2 delete-nat-gateway --nat-gateway-id "$natgateway_id" >/dev/null
}

create_security_group() {
  local sg_name="$1"
  local vpc_id="$2"
  sg_id=$(aws ec2 create-security-group --group-name "$sg_name" \
    --description "cluster security group" \
    --vpc-id "$vpc_id" \
    --tag-specifications "ResourceType=security-group,Tags=[{Key=Name,Value=$sg_name}]" \
    --query "{GroupId:GroupId}" \
    --output text)
  echo "$sg_id"
}

get_security_group_id() {
  local sg_name="$1"
  sg_id=$(aws ec2 describe-security-groups \
    --filter "Name=tag:Name,Values=$sg_name" \
    --query "SecurityGroups[].GroupId" \
    --output text)
  echo "$sg_id"
}

delete_security_group() {
  local sg_id="$1"
  aws ec2 delete-security-group --group-id "$sg_id" >/dev/null
}

create_rule() {
  local sg_id="$1"
  local protocol="$2"
  local port="$3"
  local cidr="$4"
  aws ec2 authorize-security-group-ingress --group-id "$sg_id" \
    --protocol "$protocol" \
    --port "$port" \
    --cidr "$cidr" >/dev/null
}

while getopts "tcd" flag; do
  case "${flag}" in
  t) ;;

  c)
    cluster_name="spotifyrun"
    region="eu-north-1"
    vpc_name="spotifyrun"
    igw_name="spotifyrun"
    vpc_cidr_block="10.0.0.0/16"
    subnet_public_a_name="public-eu-north-1a"
    subnet_public_a_cidr="10.0.1.0/24"
    subnet_private_a_name="private-eu-north-1a"
    subnet_private_a_cidr="10.0.3.0/24"
    availability_zone_a="eu-north-1a"
    route_table_public_name="public-eu-north-1"
    route_table_private_a_name="private-eu-north-1a"
    eip_network_border_group="eu-north-1"
    eip_a_name="eu-north-1a"
    ngw_a_name="public-eu-north-1a"
    sg_name="spotifyrun"

    # Create VPC
    echo "Creating VPC $vpc_name in region $region with CIDR $vpc_cidr_block for cluster $cluster_name"
    vpc_id=$(create_vpc $vpc_name $cluster_name $region $vpc_cidr_block)
    echo "Created VPC $vpc_id"

    # Enable VPC DNS hostname
    echo "Enabling DNS hostnames for $vpc_id"
    enable_dns_hostname "$vpc_id"
    echo "Enabled DNS hostnames for $vpc_id"

    # Create IGW
    echo "Creating IGW $igw_name in region $region"
    igw_id=$(create_internet_gateway $igw_name $region)
    echo "Created IGW $igw_id"

    # Attach IGW to VPC
    echo "Attaching IGW $igw_id to VPC $vpc_id"
    attach_internet_gateway "$vpc_id" "$igw_id"
    echo "Attached IGW $igw_id to VPC $vpc_id"

    # Creating subnets
    subnet_public_elb_tag="{Key=kubernetes.io/role/elb,Value=1}"
    subnet_private_elb_tag="{Key=kubernetes.io/role/internal-elb,Value=1}"

    # Create public subnet a
    echo "Creating subnet $subnet_public_a_name in availability zone $availability_zone_a with CIDR $subnet_public_a_cidr"
    subnet_public_a_id=$(create_subnet "$subnet_public_a_name" "$subnet_public_a_cidr" "$availability_zone_a" "$vpc_id" "$subnet_public_elb_tag")
    echo "Created subnet $subnet_public_a_id"

    # Create private subnet a
    echo "Creating subnet $subnet_private_a_name in availability zone $availability_zone_a with CIDR $subnet_private_a_cidr"
    subnet_private_a_id=$(create_subnet "$subnet_private_a_name" "$subnet_private_a_cidr" "$availability_zone_a" "$vpc_id" "$subnet_private_elb_tag")
    echo "Created subnet $subnet_private_a_id"

    # Enable public subnet a IPv4 auto-assigning
    echo "Enabling public IPv4 address auto-assigning for $subnet_public_a_id"
    enable_subnet_public_ipv4_address_auto_assign "$subnet_public_a_id"
    echo "Enabled public IPv4 address auto-assigning for $subnet_public_a_id"

    # Create route table public
    echo "Creating route table $route_table_public_name"
    route_table_public_id=$(create_route_table $route_table_public_name "$vpc_id")
    echo "Created route table $route_table_public_id"

    # Create route from any IP address to IGW
    echo "Creating route from any IPv4 address to IGW $igw_id in route table $route_table_public_id"
    create_internet_gateway_route "$route_table_public_id" "$igw_id" "0.0.0.0/0"
    echo "Created route"

    # Create association between public route table and subnet a
    echo "Creating association between route table $route_table_public_id and subnet $subnet_public_a_id"
    route_association_public_a_id=$(create_route_table_association "$route_table_public_id" "$subnet_public_a_id")
    echo "Created association $route_association_public_a_id"

    # Allocate EIP address
    echo "Allocating EIP address"
    eip_a_id=$(allocate_eip $eip_a_name $eip_network_border_group)
    echo "Allocated EIP $eip_a_id"

    # Create NGW
    echo "Creating NGW $ngw_a_name"
    ngw_a_id=$(create_natgateway "$ngw_a_name" "$subnet_public_a_id" "$eip_a_id")
    poll_natgateway_state "$ngw_a_id" "available"
    echo "Created NGW $ngw_a_id"

    # Create route table private a
    echo "Creating route table $route_table_private_a_name"
    route_table_private_a_id=$(create_route_table $route_table_private_a_name "$vpc_id")
    echo "Created route table $route_table_private_a_id"

    # Create route from any IP address to NGW
    echo "Creating route from any IPv4 address to NGW $ngw_a_id in route table $route_table_private_a_id"
    create_natgateway_route "$route_table_private_a_id" "$ngw_a_id" "0.0.0.0/0"
    echo "Created route"

    # Create association between private route table a and private subnet a
    echo "Creating association between route table $route_table_private_a_id and subnet $subnet_private_a_name"
    route_association_private_a_id=$(create_route_table_association "$route_table_private_a_id" "$subnet_private_a_id")
    echo "Created association $route_association_private_a_id"

    # Create security group
    echo "Creating security group $sg_name"
    sg_id=$(create_security_group "$sg_name" "$vpc_id")
    echo "Created security group $sg_id"

    # Create security group rules
    echo "Creating rule for security group $sg_id to allow traffic on tcp port 80"
    create_rule "$sg_id" "tcp" "80" "0.0.0.0/0"
    echo "Created rule"

    echo "Creating rule for security $sg_name to allow traffic on tcp port 443"
    create_rule "$sg_id" "tcp" "443" "0.0.0.0/0"
    echo "Created rule"

    echo "Creating rule for security $sg_name to allow traffic on tcp port 22"
    create_rule "$sg_id" "tcp" "22" "0.0.0.0/0"
    echo "Created rule"

    echo "Creating rule for security $sg_name to allow ping ip"
    create_rule "$sg_id" "icmp" "all" "0.0.0.0/0"
    echo "Created rule"
    ;;
  d)
    cluster_name="spotifyrun"
    region="eu-north-1"
    vpc_name="spotifyrun"
    igw_name="spotifyrun"
    vpc_cidr_block="10.0.0.0/16"
    subnet_public_a_name="public-eu-north-1a"
    subnet_private_a_name="private-eu-north-1a"
    route_table_public_name="public-eu-north-1"
    route_table_private_a_name="private-eu-north-1a"
    eip_a_name="eu-north-1a"
    ngw_a_name="public-eu-north-1a"
    sg_name="spotifyrun"

    # Find VPC
    echo "Looking for VPC $vpc_name"
    vpc_id=$(get_vpc_id $vpc_name)
    echo "Found VPC $vpc_id"

    # Find IGW
    echo "Looking for IGW $vpc_name"
    igw_id=$(get_internet_gateway_id $igw_name)
    echo "Found IGW $igw_id"

    # Find public route table
    echo "Looking for route table $route_table_public_name"
    route_table_public_id=$(get_route_table_id "$vpc_id" $route_table_public_name)
    echo "Found route table $route_table_public_id"

    # Delete route to IGW
    echo "Deleting route from any IPv4 address to IGW $igw_id in route table $route_table_public_id"
    delete_route "$route_table_public_id" "0.0.0.0/0"
    echo "Deleted route"

    # Find public subnet a
    echo "Looking for subnet $subnet_public_a_name"
    subnet_public_a_id=$(get_subnet_id $subnet_public_a_name)
    echo "Found subnet $subnet_public_a_id"

    # Find public subnet a and public route table association
    echo "Looking for association between subnet $subnet_public_a_id and route table $route_table_public_id"
    route_association_public_a_id=$(get_route_table_association_id "$route_table_public_id" "$subnet_public_a_id")
    echo "Found association $route_association_public_a_id"

    # Delete subnet a and public route table association
    echo "Deleting association $route_association_public_a_id"
    delete_route_table_association "$route_association_public_a_id"
    echo "Deleted association $route_association_public_a_id"

    # Delete public route table
    echo "Deleting route table $route_table_public_id"
    delete_route_table "$route_table_public_id"
    echo "Deleted route table $route_table_public_id"

    # Find NGW
    echo "Looking for NGW $ngw_a_name"
    ngw_a_id=$(get_natgateway_id "$ngw_a_name")
    echo "Found NGW $ngw_a_id"

    # Find private route table
    echo "Looking for route table $route_table_private_a_name"
    route_table_private_a_id=$(get_route_table_id "$vpc_id" $route_table_private_a_name)
    echo "Found route table $route_table_private_a_id"

    # Delete route to NGW
    echo "Deleting route from any IPv4 address to NGW $ngw_a_id in route table $route_table_private_a_id"
    delete_route "$route_table_private_a_id" "0.0.0.0/0"
    echo "Deleted route"

    # Find private subnet a
    echo "Looking for subnet $subnet_private_a_name"
    subnet_private_a_id=$(get_subnet_id $subnet_private_a_name)
    echo "Found subnet $subnet_private_a_id"

    # Find private subnet a and private route a table association
    echo "Looking for association between subnet $subnet_private_a_id and route table $route_table_private_a_id"
    route_association_private_a_id=$(get_route_table_association_id "$route_table_private_a_id" "$subnet_private_a_id")
    echo "Found association $route_association_private_a_id"

    # Delete private subnet a and private route table association
    echo "Deleting association $route_association_private_a_id"
    delete_route_table_association "$route_association_private_a_id"
    echo "Deleted association $route_association_private_a_id"

    # Delete private route a table
    echo "Deleting route table $route_table_private_a_id"
    delete_route_table "$route_table_private_a_id"
    echo "Deleted route table $route_table_private_a_id"

    # Delete NGW
    echo "Deleting NGW $ngw_a_id"
    delete_natgateway "$ngw_a_id"
    poll_natgateway_state "$ngw_a_id" "deleted"
    echo "Deleted NGW $ngw_a_id"

    # Delete public subnet a
    echo "Deleting subnet $subnet_public_a_id"
    delete_subnet "$subnet_public_a_id"
    echo "Deleted subnet $subnet_public_a_id"

    # Delete private subnet a
    echo "Deleting subnet $subnet_private_a_id"
    delete_subnet "$subnet_private_a_id"
    echo "Deleted subnet $subnet_private_a_id"

    # Detach IGW
    echo "Detaching IGW $igw_id from VPC $vpc_id"
    detach_internet_gateway "$vpc_id" "$igw_id"
    echo "Detached IGW $igw_id from VPC $vpc_id"

    # Delete IGW
    echo "Deleting IGW $igw_id"
    delete_internet_gateway "$igw_id"
    echo "Deleted IGW $igw_id"

    # Find security group
    echo "Looking for security group $sg_name"
    sg_id=$(get_security_group_id $sg_name)
    echo "Found security group $sg_id"

    # Delete security group
    echo "Deleting security group $sg_id"
    delete_security_group "$sg_id"
    echo "Deleted security group $sg_id"

    # Delete VPC
    echo "Deleting VPC $vpc_id"
    delete_vpc "$vpc_id"
    echo "Deleted VPC $vpc_id"

    # Find EIP
    echo "Looking for EIP $eip_a_name"
    eip_a_id=$(get_eip_id $eip_a_name)
    echo "Found EIP $eip_a_id"

    # Release EIP
    echo "Releasing EIP $eip_a_id"
    release_eip_id "$eip_a_id"
    echo "Released EIP $eip_a_id"

    ;;
  \?)
    describe_flags
    ;;
  esac
done
