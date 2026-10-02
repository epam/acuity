output "ecr_repository_urls" {
  description = "repo-name => repository_url. acuity/terraform reads these back by name via data \"aws_ecr_repository\", not remote state."
  value       = { for k, m in module.ecr : k => m.repository_url }
}

output "transfer_bucket_name" {
  description = "acuity/terraform reads this back by name via data \"aws_s3_bucket\", not remote state."
  value       = module.transfer_bucket.s3_bucket_id
}
