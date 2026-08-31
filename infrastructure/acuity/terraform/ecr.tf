locals {
  aws_ecr_repo_prefix = "epm-lstr-acuity"
  ecr_repos           = toset(["flyway", "admin", "va-hub", "va-hub-ui", "va-security"])
}

module "ecr" {
  source   = "terraform-aws-modules/ecr/aws"
  version  = "~> 3.2"
  for_each = local.ecr_repos

  repository_name                 = "${local.aws_ecr_repo_prefix}/${each.key}"
  repository_image_tag_mutability = "IMMUTABLE"

  # No image expiry - prune by hand, or set repository_lifecycle_policy
  # if ECR storage cost ever matters (5 repos of rare releases: negligible).
  create_lifecycle_policy = false
}
