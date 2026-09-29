# Repos are provisioned once in infrastructure/terraform/bootstrap, not
# per-workspace - they're shared across every workspace and owned by the
# image-build pipeline. This just reads them back by name.
locals {
  aws_ecr_repo_prefix = "epm-lstr-acuity"
  ecr_repos           = toset(["flyway", "admin", "va-hub", "va-hub-ui"])
}

data "aws_ecr_repository" "app" {
  for_each = local.ecr_repos
  name     = "${local.aws_ecr_repo_prefix}/${each.key}"
}
