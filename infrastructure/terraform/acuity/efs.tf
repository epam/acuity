
module "efs" {
  source  = "terraform-aws-modules/efs/aws"
  version = "~> 2.0"

  name = local.name_prefix

  create_security_group = false

  # Keyed by AZ: for_each keys must be known at plan time (subnet IDs are not).
  mount_targets = {
    for idx, az in local.azs : az => {
      subnet_id       = module.vpc.public_subnets[idx]
      security_groups = [module.sg_efs.id]
    }
  }

  # PoC data is disposable: no backups/PITR.
  create_backup_policy = false
}
