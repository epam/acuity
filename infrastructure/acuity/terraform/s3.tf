module "transfer_bucket" {
  source  = "terraform-aws-modules/s3-bucket/aws"
  version = "~> 5.0"

  bucket = "acuity-poc-transfer-${data.aws_caller_identity.current.account_id}"

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true

  control_object_ownership = true
  object_ownership         = "BucketOwnerEnforced"

  attach_deny_insecure_transport_policy = true

  server_side_encryption_configuration = {
    rule = {
      apply_server_side_encryption_by_default = {
        sse_algorithm = "AES256"
      }
    }
  }
}

# Bastion's own role (bastion.tf) gets read/write on this bucket only.
data "aws_iam_policy_document" "bastion_transfer_access" {
  statement {
    sid       = "ListTransferBucket"
    actions   = ["s3:ListBucket"]
    resources = [module.transfer_bucket.s3_bucket_arn]
  }

  statement {
    sid       = "ReadWriteTransferObjects"
    actions   = ["s3:GetObject", "s3:PutObject", "s3:DeleteObject"]
    resources = ["${module.transfer_bucket.s3_bucket_arn}/*"]
  }
}

resource "aws_iam_role_policy" "bastion_transfer_access" {
  name   = "acuity-poc-bastion-transfer-access"
  role   = aws_iam_role.bastion.id
  policy = data.aws_iam_policy_document.bastion_transfer_access.json
}

output "transfer_bucket_name" {
  description = "S3 staging bucket for local<->bastion file transfer."
  value       = module.transfer_bucket.s3_bucket_id
}
