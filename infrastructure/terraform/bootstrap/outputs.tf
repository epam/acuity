output "ecr_repository_urls" {
  value = { for k, m in module.ecr : k => m.repository_url }
}

output "public_zone" {
  value = { zone_id = aws_route53_zone.public.zone_id, name_servers = aws_route53_zone.public.name_servers }
}

output "transfer_bucket_name" {
  value = module.transfer_bucket.s3_bucket_id
}
