
module "rds" {
  source  = "terraform-aws-modules/rds/aws"
  version = "~> 7.0"

  identifier = local.name_prefix_hyphen

  engine         = "postgres"
  engine_version = "17"
  family         = "postgres17"
  instance_class = "db.t4g.small"

  allocated_storage = 20
  storage_type      = "gp3"

  db_name  = "acuity_db"
  username = "dbadmin"
  port     = 5432

  multi_az            = false # single-AZ: PoC cost ceiling.
  publicly_accessible = false

  # random_password is stateful (ephemeral re-rolls per run, so RDS and SSM drifted). Bump *_wo_version here and in ssm.tf to rotate.
  manage_master_user_password = false
  password_wo                 = random_password.dbadmin_password.result
  password_wo_version         = 2

  # Public subnets; guarded only by publicly_accessible=false + sg_rds.
  create_db_subnet_group = true
  subnet_ids             = module.vpc.public_subnets
  vpc_security_group_ids = [module.sg_rds.id]

  create_db_option_group = false
  parameters = [
    { name = "password_encryption", value = "md5", apply_method = "immediate" },
    # SSL not enforced: image JDBC URLs carry no ssl=.
    { name = "rds.force_ssl", value = "0", apply_method = "immediate" },
  ]

  backup_retention_period = 7
  deletion_protection     = false
  skip_final_snapshot     = true
}
