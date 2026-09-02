# EFS so admin's uploads survive Fargate task restarts (Story 1.7 / AD-10).

module "efs" {
  source  = "terraform-aws-modules/efs/aws"
  version = "~> 2.0"

  name = "acuity-poc"

  # sg-efs already exists (network.tf, Story 1.1); attach it per mount target.
  create_security_group = false

  # One mount target per public subnet; key by subnet id so nothing depends on
  # the order two separate lists happen to be in.
  mount_targets = {
    for s in module.vpc.public_subnets : s => {
      subnet_id       = s
      security_groups = [module.sg_efs.id]
    }
  }

  # PoC upload data is disposable demo content - no automatic backups / PITR.
  # Re-enable (the module default) before any non-PoC use.
  create_backup_policy = false
}
