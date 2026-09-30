output "bastion_instance_id" {
  description = "SSM target ID for `aws ssm start-session --target <id>`."
  value       = aws_instance.bastion.id
}

output "transfer_bucket_name" {
  description = "S3 staging bucket for local<->bastion file transfer (provisioned in infrastructure/terraform/bootstrap)."
  value       = data.aws_s3_bucket.transfer.id
}

output "alb_address" {
  description = "DNS name of the created ALB"
  value       = module.alb.dns_name
}
