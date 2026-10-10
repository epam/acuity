# Bootstrap

Account-level resources shared by every `infrastructure/terraform/acuity`
workspace:

- the ECR repos (owned by the image-build pipeline's naming, not any one
  environment);
- the S3 transfer bucket (one shared staging area, not per-environment);
- the Route 53 public zone and the wildcard ACM certificate for workspace
  hostnames;
- the GitHub OIDC IAM: the GitHub Actions OIDC provider, the
  `pocf_acuity-boundary` permissions boundary and the `pocf_acuity-github-*`
  CI roles (see [GitHub OIDC](#github-oidc)).

Not workspace-scoped — apply once, outside any `terraform workspace`.

```sh
cd infrastructure/terraform/bootstrap
terraform init
terraform apply
```

Apply this **before** the first `infrastructure/terraform/acuity` workspace
apply — that root only reads these resources back by name
(`data "aws_ecr_repository"`, `data "aws_s3_bucket"`) and fails fast if they
don't exist yet.

## GitHub OIDC

Defined in `iam.tf`. `bootstrap` is applied only from a laptop, under your own
`AWS_PROFILE`. CI never plans or applies `bootstrap`: it defines the roles CI
assumes, so CI applying it could widen its own permissions.

### Provider

`token.actions.githubusercontent.com`, client ID `sts.amazonaws.com`, no
thumbprints. AWS allows one provider per URL per account, and this is the
account's only GitHub provider. Other streams must reuse it rather than
recreate it. It has `prevent_destroy`, because deleting it breaks every role
that trusts it.
