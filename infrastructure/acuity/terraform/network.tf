data "aws_availability_zones" "available" {
  state = "available"
}

locals {
  azs            = slice(data.aws_availability_zones.available.names, 0, 2)
  public_subnets = ["10.0.0.0/24", "10.0.1.0/24"]
  app_port       = 8000 # va-hub-ui, va-hub, admin, va-security all listen here

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

  name = "acuity-poc"
  cidr = "10.0.0.0/16"

  azs                     = local.azs
  public_subnets          = local.public_subnets
  map_public_ip_on_launch = true

  enable_nat_gateway = false

  # Epam Org policy forbids any Terraform-managed change to the default Network ACL;
  # a central governance system owns and enforces its rules and auto-reverts
  # anything else. 
  # The module manages and overwrites it by default, so opt out entirely and touch nothing.
  manage_default_network_acl = false
}

# ALB is the only internet-facing resource, restricted to the corporate VPN
module "sg_alb" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-alb"
  description = "ALB - inbound from the corporate VPN only"

  vpc_id = module.vpc.vpc_id

  ingress_rules = merge(
    { for cidr in var.vpn_cidrs : "http-${replace(cidr, "/[./]/", "-")}" => {
      description = "va-hub-ui / va-hub HTTP from VPN"
      ip_protocol = "tcp"
      from_port   = 80
      to_port     = 80
      cidr_ipv4   = cidr
    } },
    { for cidr in var.vpn_cidrs : "admin-${replace(cidr, "/[./]/", "-")}" => {
      description = "admin HTTP from VPN"
      ip_protocol = "tcp"
      from_port   = 9090
      to_port     = 9090
      cidr_ipv4   = cidr
    } },
  )

  egress_rules = local.egress_all
}

# va-hub-ui: reached only by the ALB
module "sg_va_hub_ui" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-va-hub-ui"
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

  name        = "acuity-poc-va-hub"
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

# admin: hit only by the ALB (:9090 listener)
module "sg_admin" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-admin"
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

# va-security: never reached from the ALB - only va-hub and admin call it.
module "sg_va_security" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-va-security"
  description = "va-security task - inbound from va-hub and admin only"

  vpc_id = module.vpc.vpc_id

  ingress_rules = {
    from_va_hub = {
      description                  = "va-hub - va-security"
      ip_protocol                  = "tcp"
      from_port                    = local.app_port
      to_port                      = local.app_port
      referenced_security_group_id = module.sg_va_hub.id
    }
    from_admin = {
      description                  = "admin - va-security"
      ip_protocol                  = "tcp"
      from_port                    = local.app_port
      to_port                      = local.app_port
      referenced_security_group_id = module.sg_admin.id
    }
  }

  egress_rules = local.egress_all
}

# flyway: one-off migration task, no inbound; egress narrows to RDS-only once its task exists 
module "sg_flyway" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-flyway"
  description = "flyway migration task - no inbound"

  vpc_id = module.vpc.vpc_id

  ingress_rules = {}
  egress_rules  = local.egress_all
}

# rds: only the app/migration tasks and the bastion may reach it.
module "sg_rds" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-rds"
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
    from_va_security = {
      description                  = "va-security - RDS"
      ip_protocol                  = "tcp"
      from_port                    = 5432
      to_port                      = 5432
      referenced_security_group_id = module.sg_va_security.id
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

  # RDS never initiates outbound connections (no log exports, no replicas, no S3 import/export extensions here).
  # Revisit if any of those get added.
  egress_rules = {}
}

# efs: only admin mounts it
module "sg_efs" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-efs"
  description = "EFS - inbound from admin only"

  vpc_id = module.vpc.vpc_id

  ingress_rules = {
    from_admin = {
      description                  = "admin - EFS (NFS)"
      ip_protocol                  = "tcp"
      from_port                    = 2049
      to_port                      = 2049
      referenced_security_group_id = module.sg_admin.id
    }
  }

  # EFS mount targets are receive-only (NFS server side); nothing to egress.
  egress_rules = {}
}

# bastion: no inbound rules at all - reached only via SSM Session Manager.
module "sg_bastion" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-bastion"
  description = "Bastion - no inbound; accessed only via SSM Session Manager"

  vpc_id = module.vpc.vpc_id

  ingress_rules = {}

  egress_rules = local.egress_all
}
