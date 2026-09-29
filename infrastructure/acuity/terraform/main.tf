terraform {
  required_version = ">= 1.15"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }

  backend "s3" {
    region       = "us-east-1"
    bucket       = "epm-lstr-terraform-state-us-east-1"
    key          = "pocf_acuity-terraform.tfstate" # per-workspace isolation via native workspace_key_prefix (default "env:/<workspace>/<key>")
    use_lockfile = true
  }
}

locals {
  name_prefix        = "pocf_acuity-${terraform.workspace}" # underscore+hyphen-safe resources
  name_prefix_hyphen = "pocf-acuity-${terraform.workspace}" # ALB, target groups, RDS id, S3 bucket
}

# Blocks plan/apply on the "default" workspace so every resource is always
# named/tagged from a real workspace - see README.md.
resource "terraform_data" "workspace_guard" {
  input = terraform.workspace

  lifecycle {
    precondition {
      condition     = terraform.workspace != "default"
      error_message = "Refusing to plan/apply in the \"${terraform.workspace}\" workspace. Select or create a named workspace first, e.g. `terraform workspace new test`."
    }
    precondition {
      # Keeps every derived name inside AWS's tightest limit (the ALB name,
      # "pocf-acuity-<ws>-alb", caps at 32 chars) and out of RDS/S3's
      # lowercase-only charsets, so a bad workspace name fails fast here
      # instead of deep inside an apply.
      condition     = can(regex("^[a-z][a-z0-9-]{0,15}$", terraform.workspace))
      error_message = "Workspace name \"${terraform.workspace}\" must be lowercase alnum/hyphen, start with a letter, and be at most 16 characters (AWS resource name limits)."
    }
  }
}

provider "aws" {
  region = "us-east-1"

  default_tags {
    tags = {
      Project   = "EPM-LSTR"
      Stream    = "POCF_ACUITY"
      Env       = terraform.workspace
      Terraform = true
    }
  }
}
