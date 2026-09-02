data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

data "aws_kms_alias" "ssm" {
  name = "alias/aws/ssm"
}

resource "random_password" "dbadmin_password" {
  length  = 32 # within RDS's 8-128 master-password limit, ample entropy
  special = false
}

resource "random_password" "acuity_password" {
  length  = 32
  special = false
}

resource "aws_ssm_parameter" "dbadmin_password" {
  name        = "/acuity/poc/db/DBADMIN_PASSWORD"
  description = "RDS master password"
  type        = "SecureString"
  value       = random_password.dbadmin_password.result
}

resource "aws_ssm_parameter" "acuity_password" {
  name        = "/acuity/poc/db/ACUITY_PASSWORD"
  description = "Acuity app-role password"
  type        = "SecureString"
  value       = random_password.acuity_password.result
}

data "aws_iam_policy_document" "ssm_secrets_read" {
  statement {
    sid       = "ReadAcuityPocParameters"
    actions   = ["ssm:GetParameters"]
    resources = ["arn:aws:ssm:${data.aws_region.current.region}:${data.aws_caller_identity.current.account_id}:parameter/acuity/poc/*"]
  }

  statement {
    sid       = "DecryptWithSsmKey"
    actions   = ["kms:Decrypt"]
    resources = [data.aws_kms_alias.ssm.target_key_arn]

    condition {
      test     = "StringEquals"
      variable = "kms:ViaService"
      values   = ["ssm.${data.aws_region.current.region}.amazonaws.com"]
    }
  }
}

resource "aws_iam_policy" "ssm_secrets_read" {
  name        = "acuity-poc-ssm-secrets-read"
  description = "Read the Acuity PoC DB secrets from SSM Parameter Store and decrypt them with the aws/ssm key."
  policy      = data.aws_iam_policy_document.ssm_secrets_read.json
}
