terraform {
  required_version = ">= 1.15"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }

  backend "s3" {
    region       = "us-east-1"
    bucket       = "epm-lstr-terraform-state-45116284"
    key          = "acuity/terraform.tfstate"
    use_lockfile = true
  }
}

provider "aws" {
  region = "us-east-1"

  default_tags {
    tags = {
      Project   = "acuity"
      Env       = "poc"
      Terraform = true
    }
  }
}
