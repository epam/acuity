ephemeral "random_password" "dbadmin_password_v005" {
  length  = 32 # within RDS's 8-128 master-password limit, ample entropy
  special = false
}

ephemeral "random_password" "acuity_password_v005" {
  length  = 32
  special = false
}

resource "aws_ssm_parameter" "dbadmin_password_v005" {
  name             = "/acuity/${var.deployment}/db/DBADMIN_PASSWORD"
  description      = "RDS master password"
  type             = "SecureString"
  value_wo         = ephemeral.random_password.dbadmin_password_v005.result
  value_wo_version = 3
}

resource "aws_ssm_parameter" "acuity_password_v005" {
  name             = "/acuity/${var.deployment}/db/ACUITY_PASSWORD"
  description      = "Acuity app-role password"
  type             = "SecureString"
  value_wo         = ephemeral.random_password.acuity_password_v005.result
  value_wo_version = 3
}

data "aws_iam_policy_document" "ssm_secrets_read_v005" {
  statement {
    sid       = "ReadAcuity${var.deployment}Parameters"
    actions   = ["ssm:GetParameters"]
    resources = ["arn:aws:ssm:${data.aws_region.current.region}:${data.aws_caller_identity.current.account_id}:parameter/acuity/${var.deployment}/*"]
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

resource "aws_iam_policy" "ssm_secrets_read_v005" {
  name        = "acuity-${var.deployment}-ssm-secrets-read"
  description = "Read the Acuity ${var.deployment} DB secrets from SSM Parameter Store and decrypt them with the aws/ssm key."
  policy      = data.aws_iam_policy_document.ssm_secrets_read_v005.json
}
