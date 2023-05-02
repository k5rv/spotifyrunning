#!/bin/bash
#
# Creates AWS resources: VPC, internet gateway, public and private subnets, natgateway, route tables, security group
source ./utils.sh

region="eu-north-1"
cluster_name="spotifyrun"
vpc_name="spotifyrun"
vpc_cidr_block="10.0.0.0/16"
igw_name="spotifyrun"
ngw_a_name="public-eu-north-1a"
ngw_b_name="public-eu-north-1b"
availability_zone_a="eu-north-1a"
availability_zone_b="eu-north-1b"
subnet_public_a_name="public-eu-north-1a"
subnet_public_b_name="public-eu-north-1b"
subnet_private_a_name="private-eu-north-1a"
subnet_private_b_name="private-eu-north-1b"
subnet_public_a_cidr="10.0.1.0/24"
subnet_public_b_cidr="10.0.2.0/24"
subnet_private_a_cidr="10.0.3.0/24"
subnet_private_b_cidr="10.0.4.0/24"
route_table_public_name="public-eu-north-1"
route_table_private_a_name="private-eu-north-1a"
route_table_private_b_name="private-eu-north-1b"
eip_a_name="eu-north-1a"
eip_b_name="eu-north-1b"
eip_network_border_group="eu-north-1"
sg_name="spotifyrun"
subnet_public_elb_tag="{Key=kubernetes.io/role/elb,Value=1}"
subnet_private_elb_tag="{Key=kubernetes.io/role/internal-elb,Value=1}"

describe_flags() {
  echo "Available flags:"
  echo "-d: delete VPC and its dependencies"
  echo "-c: create VPC and its dependencies"
}

while getopts "cd" flag; do
  case "${flag}" in
  c)
    # Create VPC
    echo "Creating VPC $vpc_name in region $region with CIDR $vpc_cidr_block for cluster $cluster_name"
    vpc_id=$(create_vpc "$vpc_name" "$cluster_name" "$region" $vpc_cidr_block)
    echo "Created VPC $vpc_id"

    # Enable VPC DNS hostname
    echo "Enabling DNS hostnames for $vpc_id"
    enable_dns_hostnames "$vpc_id"
    echo "Enabled DNS hostnames for $vpc_id"

    # Create IGW
    echo "Creating IGW $igw_name in region $region"
    igw_id=$(create_internet_gateway "$igw_name" "$region")
    echo "Created IGW $igw_id"

    # Attach IGW to VPC
    echo "Attaching IGW $igw_id to VPC $vpc_id"
    attach_internet_gateway "$vpc_id" "$igw_id"
    echo "Attached IGW $igw_id to VPC $vpc_id"

    # Create public subnet A
    echo "Creating subnet $subnet_public_a_name in availability zone $availability_zone_a with CIDR $subnet_public_a_cidr"
    subnet_public_a_id=$(create_subnet "$subnet_public_a_name" "$subnet_public_a_cidr" "$availability_zone_a" "$vpc_id" "$subnet_public_elb_tag")
    echo "Created subnet $subnet_public_a_id"

    # Create public subnet B
    echo "Creating subnet $subnet_public_b_name in availability zone $availability_zone_b with CIDR $subnet_public_b_cidr"
    subnet_public_b_id=$(create_subnet "$subnet_public_b_name" "$subnet_public_b_cidr" "$availability_zone_b" "$vpc_id" "$subnet_public_elb_tag")
    echo "Created subnet $subnet_public_b_id"

    # Create private subnet A
    echo "Creating subnet $subnet_private_a_name in availability zone $availability_zone_a with CIDR $subnet_private_a_cidr"
    subnet_private_a_id=$(create_subnet "$subnet_private_a_name" "$subnet_private_a_cidr" "$availability_zone_a" "$vpc_id" "$subnet_private_elb_tag")
    echo "Created subnet $subnet_private_a_id"

    # Create private subnet B
    echo "Creating subnet $subnet_private_b_name in availability zone $availability_zone_b with CIDR $subnet_private_b_cidr"
    subnet_private_b_id=$(create_subnet "$subnet_private_b_name" "$subnet_private_b_cidr" "$availability_zone_b" "$vpc_id" "$subnet_private_elb_tag")
    echo "Created subnet $subnet_private_b_id"

    # Enable public subnet A IPv4 auto-assigning
    echo "Enabling public IPv4 address auto-assigning for $subnet_public_a_id"
    enable_public_ipv4_address_auto_assign "$subnet_public_a_id"
    echo "Enabled public IPv4 address auto-assigning for $subnet_public_a_id"

    # Enable public subnet B IPv4 auto-assigning
    echo "Enabling public IPv4 address auto-assigning for $subnet_public_b_id"
    enable_public_ipv4_address_auto_assign "$subnet_public_b_id"
    echo "Enabled public IPv4 address auto-assigning for $subnet_public_b_id"

    # Create route table public
    echo "Creating route table $route_table_public_name"
    route_table_public_id=$(create_route_table $route_table_public_name "$vpc_id")
    echo "Created route table $route_table_public_id"

    # Create route from any IP address to IGW
    echo "Creating route from any IPv4 address to IGW $igw_id in route table $route_table_public_id"
    create_internet_gateway_route "$route_table_public_id" "$igw_id" "0.0.0.0/0"
    echo "Created route"

    # Create association between public route table and subnet A
    echo "Creating association between route table $route_table_public_id and subnet $subnet_public_a_id"
    route_association_public_a_id=$(create_route_table_association "$route_table_public_id" "$subnet_public_a_id")
    echo "Created association $route_association_public_a_id"

    # Create association between public route table and subnet B
    echo "Creating association between route table $route_table_public_id and subnet $subnet_public_b_id"
    route_association_public_b_id=$(create_route_table_association "$route_table_public_id" "$subnet_public_b_id")
    echo "Created association $route_association_public_b_id"

    # Allocate EIP A address
    echo "Allocating EIP address"
    eip_a_id=$(create_eip $eip_a_name $eip_network_border_group)
    echo "Allocated EIP $eip_a_id"

    # Allocate EIP B address
    echo "Allocating EIP address"
    eip_b_id=$(create_eip $eip_b_name $eip_network_border_group)
    echo "Allocated EIP $eip_b_id"

    # Create NGW A
    echo "Creating NGW $ngw_a_name"
    ngw_a_id=$(create_natgateway "$ngw_a_name" "$subnet_public_a_id" "$eip_a_id")

    # Create NGW B
    echo "Creating NGW $ngw_b_name"
    ngw_b_id=$(create_natgateway "$ngw_b_name" "$subnet_public_b_id" "$eip_b_id")

    # Waiting for NGW A
    poll_natgateway_state "$ngw_a_id" "available"
    echo "Created NGW $ngw_a_id"

    # Waiting for NGW B
    poll_natgateway_state "$ngw_b_id" "available"
    echo "Created NGW $ngw_b_id"

    # Create route table private A
    echo "Creating route table $route_table_private_a_name"
    route_table_private_a_id=$(create_route_table $route_table_private_a_name "$vpc_id")
    echo "Created route table $route_table_private_a_id"

    # Create route from any IP address to NGW in route table A
    echo "Creating route from any IPv4 address to NGW $ngw_a_id in route table $route_table_private_a_id"
    create_natgateway_route "$route_table_private_a_id" "$ngw_a_id" "0.0.0.0/0"
    echo "Created route"

    # Create association between private route table A and private subnet A
    echo "Creating association between route table $route_table_private_a_id and subnet $subnet_private_a_name"
    route_association_private_a_id=$(create_route_table_association "$route_table_private_a_id" "$subnet_private_a_id")
    echo "Created association $route_association_private_a_id"

    # Create route table private B
    echo "Creating route table $route_table_private_b_name"
    route_table_private_b_id=$(create_route_table $route_table_private_b_name "$vpc_id")
    echo "Created route table $route_table_private_b_id"

    # Create route from any IP address to NGW in route table B
    echo "Creating route from any IPv4 address to NGW $ngw_b_id in route table $route_table_private_b_id"
    create_natgateway_route "$route_table_private_b_id" "$ngw_b_id" "0.0.0.0/0"
    echo "Created route"

    # Create association between private route table B and private subnet B
    echo "Creating association between route table $route_table_private_b_id and subnet $subnet_private_b_name"
    route_association_private_b_id=$(create_route_table_association "$route_table_private_b_id" "$subnet_private_b_id")
    echo "Created association $route_association_private_b_id"

    # Create security group
    echo "Creating security group $sg_name"
    sg_id=$(create_security_group "$sg_name" "$vpc_id")
    echo "Created security group $sg_id"

    # Create security group rules
    echo "Creating rule for security group $sg_id to allow HTTP traffic"
    create_rule "$sg_id" "tcp" "80" "0.0.0.0/0"
    echo "Created rule"

    echo "Creating rule for security group $sg_id to allow HTTPS traffic"
    create_rule "$sg_id" "tcp" "443" "0.0.0.0/0"
    echo "Created rule"

    echo "Creating rule for security group $sg_id to allow SSH traffic"
    create_rule "$sg_id" "tcp" "22" "0.0.0.0/0"
    echo "Created rule"

    echo "Creating rule for security group $sg_id to allow Postgresql"
    create_rule "$sg_id" "tcp" "5432" "0.0.0.0/0"
    echo "Created rule"

    echo "Creating rule for security group $sg_id to allow ping ip"
    create_rule "$sg_id" "icmp" "all" "0.0.0.0/0"
    echo "Created rule"

    ;;

  d)
    # Find VPC
    echo "Looking for VPC $vpc_name"
    vpc_id=$(get_vpc_id "$vpc_name")
    echo "Found VPC $vpc_id"

    # Find IGW
    echo "Looking for IGW $vpc_name"
    igw_id=$(get_internet_gateway_id "$igw_name")
    echo "Found IGW $igw_id"

    # Find public route table
    echo "Looking for route table $route_table_public_name"
    route_table_public_id=$(get_route_table_id "$vpc_id" $route_table_public_name)
    echo "Found route table $route_table_public_id"

    # Delete route to IGW
    echo "Deleting route from any IPv4 address to IGW $igw_id in route table $route_table_public_id"
    delete_route "$route_table_public_id" "0.0.0.0/0"
    echo "Deleted route"

    # Find public subnet A
    echo "Looking for subnet $subnet_public_a_name"
    subnet_public_a_id=$(get_subnet_id $subnet_public_a_name)
    echo "Found subnet $subnet_public_a_id"

    # Find public subnet A and public route table association
    echo "Looking for association between subnet $subnet_public_a_id and route table $route_table_public_id"
    route_association_public_a_id=$(get_route_table_association_id "$route_table_public_id" "$subnet_public_a_id")
    echo "Found association $route_association_public_a_id"

    # Delete subnet a and public route table association
    echo "Deleting association $route_association_public_a_id"
    delete_route_table_association "$route_association_public_a_id"
    echo "Deleted association $route_association_public_a_id"

    # Find public subnet B
    echo "Looking for subnet $subnet_public_b_name"
    subnet_public_b_id=$(get_subnet_id $subnet_public_b_name)
    echo "Found subnet $subnet_public_b_id"

    # Find public subnet B and public route table association
    echo "Looking for association between subnet $subnet_public_b_id and route table $route_table_public_id"
    route_association_public_b_id=$(get_route_table_association_id "$route_table_public_id" "$subnet_public_b_id")
    echo "Found association $route_association_public_b_id"

    # Delete subnet B and public route table association
    echo "Deleting association $route_association_public_b_id"
    delete_route_table_association "$route_association_public_b_id"
    echo "Deleted association $route_association_public_b_id"

    # Delete public route table
    echo "Deleting route table $route_table_public_id"
    delete_route_table "$route_table_public_id"
    echo "Deleted route table $route_table_public_id"

    # Find NGW A
    echo "Looking for NGW $ngw_a_name"
    ngw_a_id=$(get_natgateway_id "$ngw_a_name")
    echo "Found NGW $ngw_a_id"

    # Find NGW B
    echo "Looking for NGW $ngw_b_name"
    ngw_b_id=$(get_natgateway_id "$ngw_b_name")
    echo "Found NGW $ngw_b_id"

    # Find private route table A
    echo "Looking for route table $route_table_private_a_name"
    route_table_private_a_id=$(get_route_table_id "$vpc_id" $route_table_private_a_name)
    echo "Found route table $route_table_private_a_id"

    # Delete route to NGW for route table private A
    echo "Deleting route from any IPv4 address to NGW $ngw_a_id in route table $route_table_private_a_id"
    delete_route "$route_table_private_a_id" "0.0.0.0/0"
    echo "Deleted route"

    # Find private subnet A
    echo "Looking for subnet $subnet_private_a_name"
    subnet_private_a_id=$(get_subnet_id $subnet_private_a_name)
    echo "Found subnet $subnet_private_a_id"

    # Find private subnet A and private route A table association
    echo "Looking for association between subnet $subnet_private_a_id and route table $route_table_private_a_id"
    route_association_private_a_id=$(get_route_table_association_id "$route_table_private_a_id" "$subnet_private_a_id")
    echo "Found association $route_association_private_a_id"

    # Delete private subnet A and private route A table association
    echo "Deleting association $route_association_private_a_id"
    delete_route_table_association "$route_association_private_a_id"
    echo "Deleted association $route_association_private_a_id"

    # Delete private route A table
    echo "Deleting route table $route_table_private_a_id"
    delete_route_table "$route_table_private_a_id"
    echo "Deleted route table $route_table_private_a_id"

    # Find private route table B
    echo "Looking for route table $route_table_private_b_name"
    route_table_private_b_id=$(get_route_table_id "$vpc_id" $route_table_private_b_name)
    echo "Found route table $route_table_private_b_id"

    # Delete route to NGW for route table private B
    echo "Deleting route from any IPv4 address to NGW $ngw_b_id in route table $route_table_private_b_id"
    delete_route "$route_table_private_b_id" "0.0.0.0/0"
    echo "Deleted route"

    # Find private subnet B
    echo "Looking for subnet $subnet_private_b_name"
    subnet_private_b_id=$(get_subnet_id $subnet_private_b_name)
    echo "Found subnet $subnet_private_b_id"

    # Find private subnet B and private route B table association
    echo "Looking for association between subnet $subnet_private_b_id and route table $route_table_private_b_id"
    route_association_private_b_id=$(get_route_table_association_id "$route_table_private_b_id" "$subnet_private_b_id")
    echo "Found association $route_association_private_b_id"

    # Delete private subnet B and private route B table association
    echo "Deleting association $route_association_private_b_id"
    delete_route_table_association "$route_association_private_b_id"
    echo "Deleted association $route_association_private_b_id"

    # Delete private route B table
    echo "Deleting route table $route_table_private_b_id"
    delete_route_table "$route_table_private_b_id"
    echo "Deleted route table $route_table_private_b_id"

    # Delete NGW A
    echo "Deleting NGW $ngw_a_id"
    delete_natgateway "$ngw_a_id"

    # Delete NGW B
    echo "Deleting NGW $ngw_b_id"
    delete_natgateway "$ngw_b_id"

    # Waiting for NGW A
    poll_natgateway_state "$ngw_a_id" "deleted"
    echo "Deleted NGW $ngw_a_id"

    # Waiting for NGW B
    poll_natgateway_state "$ngw_b_id" "deleted"
    echo "Deleted NGW $ngw_b_id"

    # Delete public subnet A
    echo "Deleting subnet $subnet_public_a_id"
    delete_subnet "$subnet_public_a_id"
    echo "Deleted subnet $subnet_public_a_id"

    # Delete public subnet B
    echo "Deleting subnet $subnet_public_b_id"
    delete_subnet "$subnet_public_b_id"
    echo "Deleted subnet $subnet_public_b_id"

    # Delete private subnet A
    echo "Deleting subnet $subnet_private_a_id"
    delete_subnet "$subnet_private_a_id"
    echo "Deleted subnet $subnet_private_a_id"

    # Delete private subnet B
    echo "Deleting subnet $subnet_private_b_id"
    delete_subnet "$subnet_private_b_id"
    echo "Deleted subnet $subnet_private_b_id"

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
    sg_id=$(get_security_group_id "$sg_name")
    echo "Found security group $sg_id"

    # Delete security group
    echo "Deleting security group $sg_id"
    delete_security_group "$sg_id"
    echo "Deleted security group $sg_id"

    # Delete VPC
    echo "Deleting VPC $vpc_id"
    delete_vpc "$vpc_id"
    echo "Deleted VPC $vpc_id"

    # Find EIP A
    echo "Looking for EIP $eip_a_name"
    eip_a_id=$(get_eip_id $eip_a_name)
    echo "Found EIP $eip_a_id"

    # Release EIP A
    echo "Releasing EIP $eip_a_id"
    delete_eip "$eip_a_id"
    echo "Released EIP $eip_a_id"

    # Find EIP B
    echo "Looking for EIP $eip_b_name"
    eip_b_id=$(get_eip_id $eip_b_name)
    echo "Found EIP $eip_b_id"

    # Release EIP B
    echo "Releasing EIP $eip_b_id"
    delete_eip "$eip_b_id"
    echo "Released EIP $eip_b_id"
    ;;
  \?)
    describe_flags
    ;;
  esac
done
