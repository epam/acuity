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
    region               = "us-east-1"
    bucket               = "epm-lstr-terraform-state-us-east-1"
    key                  = "pocf_acuity/acuity.tfstate"
    workspace_key_prefix = "pocf_acuity/acuity_env:"
    use_lockfile         = true
  }
}

locals {
  name_prefix        = "pocf_acuity-${terraform.workspace}" # underscore+hyphen-safe resources
  name_prefix_hyphen = "pocf-acuity-${terraform.workspace}" # ALB, target groups, RDS id, S3 bucket
}

# Block the "default" workspace so resources are always named from a real one (see README).
resource "terraform_data" "workspace_guard" {
  input = terraform.workspace

  lifecycle {
    precondition {
      condition     = terraform.workspace != "default"
      error_message = "Refusing to plan/apply in the \"${terraform.workspace}\" workspace. Select or create a named workspace first, e.g. `terraform workspace new test`."
    }
    precondition {
      # Keeps derived names within limits (ALB "pocf-acuity-<ws>-alb" max 32 chars; RDS/S3 lowercase) so bad names fail at plan.
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
