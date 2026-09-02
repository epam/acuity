# Story 1.3 - single-AZ PostgreSQL 17, VPC-internal. The `dbadmin` master role
# here replaces the dropped custom-Postgres image's create_db.sql.

module "rds" {
  source  = "terraform-aws-modules/rds/aws"
  version = "~> 6.0"

  identifier = "acuity-poc"

  engine         = "postgres"
  engine_version = "17"
  family         = "postgres17"
  instance_class = "db.t4g.small"

  allocated_storage = 20
  storage_type      = "gp3"

  db_name  = "acuity_db"
  username = "dbadmin"
  port     = 5432

  multi_az            = false # single-AZ: PoC cost ceiling, no HA (NFR3)
  publicly_accessible = false

  # PoC uses Story 1.2's random_password (also written to SSM), not Secrets Manager.
  manage_master_user_password = false
  password                    = random_password.dbadmin_password.result

  # DB sits in the public subnets, guarded only by publicly_accessible = false
  # + sg_rds. Upgrade path (AD-6): private subnets once they exist, which also
  # means adding a NAT gateway.
  create_db_subnet_group = true
  subnet_ids             = module.vpc.public_subnets
  vpc_security_group_ids = [module.sg_rds.id]

  create_db_option_group = false
  parameters = [
    # va-security's bundled JDBC can't do scram.
    { name = "password_encryption", value = "md5", apply_method = "immediate" },
    # image JDBC URLs carry no `ssl=`.
    { name = "rds.force_ssl", value = "0", apply_method = "immediate" },
  ]

  backup_retention_period = 7
  deletion_protection     = false
  skip_final_snapshot     = true
}
