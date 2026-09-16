# EFS so admin's uploads survive Fargate task restarts.

module "efs" {
  source  = "terraform-aws-modules/efs/aws"
  version = "~> 2.0"

  name = "acuity-poc"

  # sg-efs already exists in network.tf
  create_security_group = false

  # Keyed by AZ (known at plan time), not by subnet ID (only known after the
  # VPC creates it) - a for_each key must be knowable before apply.
  mount_targets = {
    for idx, az in local.azs : az => {
      subnet_id       = module.vpc.public_subnets[idx]
      security_groups = [module.sg_efs.id]
    }
  }

  # PoC upload data is disposable demo content - no automatic backups or PITR.
  create_backup_policy = false
}
