locals {
  aws_ecr_repo_prefix = "epm-lstr-acuity"
  ecr_repos           = toset(["flyway", "admin", "va-hub", "va-hub-ui"])
}

module "ecr" {
  source   = "terraform-aws-modules/ecr/aws"
  version  = "~> 3.2"
  for_each = local.ecr_repos

  repository_name                 = "${local.aws_ecr_repo_prefix}/${each.key}"
  repository_image_tag_mutability = "IMMUTABLE"

  # No lifecycle policy: 5 repos of rare releases, storage negligible.
  create_lifecycle_policy = false
}
