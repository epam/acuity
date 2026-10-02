data "aws_availability_zones" "available" {
  state = "available"
}

locals {
  azs            = slice(data.aws_availability_zones.available.names, 0, 2)
  public_subnets = ["10.0.0.0/24", "10.0.1.0/24"]
  app_port       = 8000 # va-hub-ui, va-hub and admin all listen here

  egress_all = {
    all_ipv4 = {
      ip_protocol = "-1"
      cidr_ipv4   = "0.0.0.0/0"
    }
  }
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 6.0"

  name = "${local.name_prefix}-vpc"
  cidr = "10.0.0.0/16"

  azs                     = local.azs
  public_subnets          = local.public_subnets
  map_public_ip_on_launch = true

  enable_nat_gateway = false

  # Org policy forbids Terraform changes to the default NACL (governance system auto-reverts); module opted out.
  manage_default_network_acl = false
}

# ALB: only internet-facing resource, VPN-restricted
module "sg_alb" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "${local.name_prefix}-sg-alb"
  description = "ALB - inbound from the corporate VPN only"

  vpc_id = module.vpc.vpc_id

  ingress_rules = merge(
    { for cidr in var.vpn_cidrs : "https-${replace(cidr, "/[./]/", "-")}" => {
      description = "va-hub-ui / va-hub HTTPS from VPN"
      ip_protocol = "tcp"
      from_port   = 443
      to_port     = 443
      cidr_ipv4   = cidr
    } },
    { for cidr in var.vpn_cidrs : "admin-${replace(cidr, "/[./]/", "-")}" => {
      description = "admin HTTPS from VPN"
      ip_protocol = "tcp"
      from_port   = 9090
      to_port     = 9090
      cidr_ipv4   = cidr
    } },
  )

  egress_rules = local.egress_all
}

module "sg_va_hub_ui" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "${local.name_prefix}-sg-va-hub-ui"
  description = "va-hub-ui task - inbound from the ALB only"

  vpc_id = module.vpc.vpc_id

  ingress_rules = {
    from_alb = {
      description                  = "ALB - va-hub-ui"
      ip_protocol                  = "tcp"
      from_port                    = local.app_port
      to_port                      = local.app_port
      referenced_security_group_id = module.sg_alb.id
    }
  }

  egress_rules = local.egress_all
}

# va-hub: hit by the ALB and by admin over Service Connect.
module "sg_va_hub" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "${local.name_prefix}-sg-va-hub"
  description = "va-hub task - inbound from the ALB and from admin via Service Connect"

  vpc_id = module.vpc.vpc_id

  ingress_rules = {
    from_alb = {
      description                  = "ALB - va-hub"
      ip_protocol                  = "tcp"
      from_port                    = local.app_port
      to_port                      = local.app_port
      referenced_security_group_id = module.sg_alb.id
    }
    from_admin = {
      description                  = "admin - va-hub"
      ip_protocol                  = "tcp"
      from_port                    = local.app_port
      to_port                      = local.app_port
      referenced_security_group_id = module.sg_admin.id
    }
  }

  egress_rules = local.egress_all
}

module "sg_admin" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "${local.name_prefix}-sg-admin"
  description = "admin task - inbound from the ALB only"

  vpc_id = module.vpc.vpc_id

  ingress_rules = {
    from_alb = {
      description                  = "ALB - admin"
      ip_protocol                  = "tcp"
      from_port                    = local.app_port
      to_port                      = local.app_port
      referenced_security_group_id = module.sg_alb.id
    }
  }

  egress_rules = local.egress_all
}

# flyway: no inbound
module "sg_flyway" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "${local.name_prefix}-sg-flyway"
  description = "flyway migration task - no inbound"

  vpc_id = module.vpc.vpc_id

  ingress_rules = {}
  egress_rules  = local.egress_all
}

# rds: only the app/migration tasks and the bastion may reach it.
module "sg_rds" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "${local.name_prefix}-sg-rds"
  description = "RDS PostgreSQL - inbound from app tasks, flyway and the bastion only"

  vpc_id = module.vpc.vpc_id

  ingress_rules = {
    from_va_hub = {
      description                  = "va-hub - RDS"
      ip_protocol                  = "tcp"
      from_port                    = 5432
      to_port                      = 5432
      referenced_security_group_id = module.sg_va_hub.id
    }
    from_admin = {
      description                  = "admin - RDS"
      ip_protocol                  = "tcp"
      from_port                    = 5432
      to_port                      = 5432
      referenced_security_group_id = module.sg_admin.id
    }
    from_flyway = {
      description                  = "flyway - RDS"
      ip_protocol                  = "tcp"
      from_port                    = 5432
      to_port                      = 5432
      referenced_security_group_id = module.sg_flyway.id
    }
    from_bastion = {
      description                  = "bastion - RDS"
      ip_protocol                  = "tcp"
      from_port                    = 5432
      to_port                      = 5432
      referenced_security_group_id = module.sg_bastion.id
    }
  }

  # RDS never initiates outbound traffic (no log exports/replicas/S3 extensions); revisit if added.
  egress_rules = {}
}

module "sg_efs" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "${local.name_prefix}-sg-efs"
  description = "EFS - inbound from admin and the bastion only"

  vpc_id = module.vpc.vpc_id

  ingress_rules = {
    from_admin = {
      description                  = "admin - EFS (NFS)"
      ip_protocol                  = "tcp"
      from_port                    = 2049
      to_port                      = 2049
      referenced_security_group_id = module.sg_admin.id
    }
    from_bastion = {
      description                  = "bastion - EFS (NFS)"
      ip_protocol                  = "tcp"
      from_port                    = 2049
      to_port                      = 2049
      referenced_security_group_id = module.sg_bastion.id
    }
  }

  egress_rules = {}
}

# bastion: no inbound rules at all - reached only via SSM Session Manager.
module "sg_bastion" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "${local.name_prefix}-sg-bastion"
  description = "Bastion - no inbound; accessed only via SSM Session Manager"

  vpc_id = module.vpc.vpc_id

  ingress_rules = {}

  egress_rules = local.egress_all
}
