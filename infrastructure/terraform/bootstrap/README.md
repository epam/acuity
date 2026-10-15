# Bootstrap

Account-level resources shared by every `infrastructure/terraform/acuity`
workspace:

- the ECR repos (owned by the image-build pipeline's naming, not any one
  environment);
- the S3 transfer bucket (one shared staging area, not per-environment);
- the Route 53 public zone and the wildcard ACM certificate for workspace
  hostnames;
- the GitHub OIDC IAM: the GitHub Actions OIDC provider and the
  `pocf_acuity-github` CI role (see [GitHub OIDC](#github-oidc)).

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

### CI role

`pocf_acuity-github`, the one role GitHub Actions assumes for
`terraform plan`/`apply`, ECR pushes and ECS deploys. Its ARN is the
`github_role_arn` output; use it as `role-to-assume` in workflows (jobs need
`permissions: id-token: write`).

- **Trust:** any workflow run in `epam/acuity`: every branch, PR, tag and
  environment (`sub` like `repo:epam/acuity:*`, `aud` = `sts.amazonaws.com`).
  Tokens from other repos are rejected.
- **Permissions:** `AdministratorAccess`. That means "CI never applies
  `bootstrap`" holds only by convention: CI could technically edit its own role.

GitHub isn't an EPAM-approved OIDC provider, so the role needs a Cloud
Governance exception of type RoleArn. The exception is filed against the ARN,
so the role has to exist first:

1. `terraform apply` here.
2. `terraform output github_role_arn`.
3. File "Exception management for Cloud Governance controls" in the Support
   Portal with that ARN.
4. Don't use the role in CI until the exception is approved.

If Governance blocks the role before the exception lands (`or2-blocked=true`
tag, `AWSDenyAll` boundary), **don't re-apply over it**: that undoes the
remediation. Get the exception approved and have the role unblocked instead.
Keep the name stable, because renaming changes the ARN the exception is bound
to.
