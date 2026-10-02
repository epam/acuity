# Bootstrap

Account-level resources shared by every `infrastructure/terraform/acuity`
workspace: the ECR repos (owned by the image-build pipeline's naming, not any
one environment) and the S3 transfer bucket (one shared staging area, not
per-environment). Not workspace-scoped — apply once, outside any
`terraform workspace`.

```sh
cd infrastructure/terraform/bootstrap
terraform init
terraform apply
```

Apply this **before** the first `infrastructure/terraform/acuity` workspace
apply — that root only reads these resources back by name
(`data "aws_ecr_repository"`, `data "aws_s3_bucket"`) and fails fast if they
don't exist yet.
