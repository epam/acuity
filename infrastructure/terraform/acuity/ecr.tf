# Repos live in terraform/bootstrap (shared across workspaces); read by name here.
locals {
  aws_ecr_repo_prefix = "epm-lstr-acuity"
  ecr_repos           = toset(["flyway", "admin", "va-hub", "va-hub-ui"])
}

data "aws_ecr_repository" "app" {
  for_each = local.ecr_repos
  name     = "${local.aws_ecr_repo_prefix}/${each.key}"
}
