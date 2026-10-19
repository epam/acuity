terraform {
  required_version = ">= 1.15"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }

  # Single global state: account-level resources shared by all acuity workspaces.
  backend "s3" {
    region       = "us-east-1"
    bucket       = "epm-lstr-terraform-state-us-east-1"
    key          = "pocf_acuity/bootstrap.tfstate"
    use_lockfile = true
  }
}

provider "aws" {
  region = "us-east-1"

  default_tags {
    tags = {
      project     = "EPM-LSTR"
      application = "POCF_ACUITY"
      environment = terraform.workspace
      owner       = "https://github.com/epam/acuity"
      terraform   = true
    }
  }
}

data "aws_caller_identity" "current" {}
