data "aws_availability_zones" "available" {
  state = "available"
}

locals {
  azs            = slice(data.aws_availability_zones.available.names, 0, 2)
  public_subnets = ["10.0.0.0/24", "10.0.1.0/24"]
  app_port       = 8000 # va-hub-ui, va-hub, admin, va-security all listen here

  # flyway's egress narrows to RDS-only once its migration task exists.
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
  vpc_id      = module.vpc.vpc_id

  ingress_rules = merge(
    { for cidr in var.vpn_cidrs : "http-${replace(cidr, "/[./]/", "-")}" => {
      from_port   = 80
      to_port     = 80
      ip_protocol = "tcp"
      cidr_ipv4   = cidr
      description = "va-hub-ui / va-hub HTTP from VPN"
    } },
    { for cidr in var.vpn_cidrs : "admin-${replace(cidr, "/[./]/", "-")}" => {
      from_port   = 9090
      to_port     = 9090
      ip_protocol = "tcp"
      cidr_ipv4   = cidr
      description = "admin HTTP from VPN"
    } },
  )

  egress_rules = local.egress_all
}

# --- va-hub-ui: reached only by the ALB; not a Service Connect member ---
module "sg_va_hub_ui" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-va-hub-ui"
  description = "va-hub-ui task - inbound from the ALB only"
  vpc_id      = module.vpc.vpc_id

  ingress_rules = {
    from_alb = {
      from_port                    = local.app_port
      to_port                      = local.app_port
      ip_protocol                  = "tcp"
      referenced_security_group_id = module.sg_alb.id
      description                  = "ALB - va-hub-ui"
    }
  }

  egress_rules = local.egress_all
}

# --- va-hub: hit by the ALB (/resources/* rule) and by admin over Service Connect ---
module "sg_va_hub" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-va-hub"
  description = "va-hub task - inbound from the ALB and from admin (Service Connect)"
  vpc_id      = module.vpc.vpc_id

  ingress_rules = {
    from_alb = {
      from_port                    = local.app_port
      to_port                      = local.app_port
      ip_protocol                  = "tcp"
      referenced_security_group_id = module.sg_alb.id
      description                  = "ALB - va-hub (/resources/*)"
    }
    from_admin = {
      from_port                    = local.app_port
      to_port                      = local.app_port
      ip_protocol                  = "tcp"
      referenced_security_group_id = module.sg_admin.id
      description                  = "admin - va-hub (Service Connect)"
    }
  }

  egress_rules = local.egress_all
}

# --- admin: hit only by the ALB (:9090 listener) ---
module "sg_admin" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-admin"
  description = "admin task - inbound from the ALB only"
  vpc_id      = module.vpc.vpc_id

  ingress_rules = {
    from_alb = {
      from_port                    = local.app_port
      to_port                      = local.app_port
      ip_protocol                  = "tcp"
      referenced_security_group_id = module.sg_alb.id
      description                  = "ALB - admin"
    }
  }

  egress_rules = local.egress_all
}

# --- va-security: never reached from the ALB - only va-hub and admin call it (Service Connect) ---
module "sg_va_security" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-va-security"
  description = "va-security task - inbound from va-hub and admin only (Service Connect)"
  vpc_id      = module.vpc.vpc_id

  ingress_rules = {
    from_va_hub = {
      from_port                    = local.app_port
      to_port                      = local.app_port
      ip_protocol                  = "tcp"
      referenced_security_group_id = module.sg_va_hub.id
      description                  = "va-hub - va-security (Service Connect)"
    }
    from_admin = {
      from_port                    = local.app_port
      to_port                      = local.app_port
      ip_protocol                  = "tcp"
      referenced_security_group_id = module.sg_admin.id
      description                  = "admin - va-security (Service Connect)"
    }
  }

  egress_rules = local.egress_all
}

# --- flyway: one-off migration task, no inbound; egress narrows to RDS-only once its task exists ---
module "sg_flyway" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-flyway"
  description = "flyway migration task - no inbound"
  vpc_id      = module.vpc.vpc_id

  ingress_rules = {}
  egress_rules  = local.egress_all
}

# --- rds: only the app/migration tasks and the bastion may reach Postgres ---
module "sg_rds" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-rds"
  description = "RDS PostgreSQL - inbound from app tasks, flyway and the bastion only"
  vpc_id      = module.vpc.vpc_id

  ingress_rules = {
    from_va_hub = {
      from_port                    = 5432
      to_port                      = 5432
      ip_protocol                  = "tcp"
      referenced_security_group_id = module.sg_va_hub.id
      description                  = "va-hub - RDS"
    }
    from_va_security = {
      from_port                    = 5432
      to_port                      = 5432
      ip_protocol                  = "tcp"
      referenced_security_group_id = module.sg_va_security.id
      description                  = "va-security - RDS"
    }
    from_admin = {
      from_port                    = 5432
      to_port                      = 5432
      ip_protocol                  = "tcp"
      referenced_security_group_id = module.sg_admin.id
      description                  = "admin - RDS"
    }
    from_flyway = {
      from_port                    = 5432
      to_port                      = 5432
      ip_protocol                  = "tcp"
      referenced_security_group_id = module.sg_flyway.id
      description                  = "flyway - RDS"
    }
    from_bastion = {
      from_port                    = 5432
      to_port                      = 5432
      ip_protocol                  = "tcp"
      referenced_security_group_id = module.sg_bastion.id
      description                  = "bastion - RDS"
    }
  }

  # RDS never initiates outbound connections (no log exports, no replicas, no
  # S3 import/export extensions here) — revisit if any of those get added.
  egress_rules = {}
}

# --- efs: only admin mounts it ---
module "sg_efs" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-efs"
  description = "EFS - inbound from admin only"
  vpc_id      = module.vpc.vpc_id

  ingress_rules = {
    from_admin = {
      from_port                    = 2049
      to_port                      = 2049
      ip_protocol                  = "tcp"
      referenced_security_group_id = module.sg_admin.id
      description                  = "admin - EFS (NFS)"
    }
  }

  # EFS mount targets are receive-only (NFS server side); nothing to egress.
  egress_rules = {}
}

# --- bastion: no inbound rules at all - reached only via SSM Session Manager ---
module "sg_bastion" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 6.0"

  name        = "acuity-poc-bastion"
  description = "Bastion - no inbound; accessed only via SSM Session Manager"
  vpc_id      = module.vpc.vpc_id

  ingress_rules = {}

  # Stays all-outbound: reaches SSM Session Manager over the public internet,
  # not a VPC endpoint.
  egress_rules = local.egress_all
}
