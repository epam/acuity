# Shared transfer bucket lives in terraform/bootstrap; read by name here.
locals {
  transfer_bucket_name = "pocf-acuity-transfer-${data.aws_caller_identity.current.account_id}"
}

data "aws_s3_bucket" "transfer" {
  bucket = local.transfer_bucket_name
}

data "aws_iam_policy_document" "bastion_transfer_access" {
  statement {
    sid       = "ListTransferBucket"
    actions   = ["s3:ListBucket"]
    resources = [data.aws_s3_bucket.transfer.arn]
  }

  statement {
    sid       = "ReadWriteTransferObjects"
    actions   = ["s3:GetObject", "s3:PutObject", "s3:DeleteObject"]
    resources = ["${data.aws_s3_bucket.transfer.arn}/*"]
  }
}

resource "aws_iam_role_policy" "bastion_transfer_access" {
  name   = "${local.name_prefix}-bastion-transfer-access"
  role   = aws_iam_role.bastion.id
  policy = data.aws_iam_policy_document.bastion_transfer_access.json
}
