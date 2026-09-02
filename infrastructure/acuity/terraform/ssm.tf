# Story 1.2 - the two AD-8 DB password secrets as SSM SecureString, plus the
# detached IAM policy that grants reading them (attached by later stories).

data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

# key_id is left unset on the parameters below, so they use this key - hence
# it's the only key the read policy needs kms:Decrypt on.
data "aws_kms_alias" "ssm" {
  name = "alias/aws/ssm"
}

# special = false: each value passes through sh -c and a SQL literal downstream.
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

# Detached read policy - nothing attaches it in this story; later stories attach
# it to their task execution roles. Scoped to the PoC's own SSM prefix and the
# single KMS key those parameters use.
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
