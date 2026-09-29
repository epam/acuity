# Workspace naming & tagging convention

This root module is workspace-driven: every AWS resource name/identifier and
tag comes from `terraform.workspace`, not a hardcoded environment literal.
That's what lets the same code target `poc`, `dev`, `stg`, etc. by switching
workspaces instead of branching the config.

## Selecting / creating a workspace

```sh
terraform workspace list                # see what exists
terraform workspace new test            # create + switch to a new one
terraform workspace select poc          # switch to an existing one
```

**The `default` workspace is blocked**, and workspace names are restricted to
lowercase letters, digits and hyphens, at most 16 characters. A precondition
in `main.tf` fails `plan`/`apply` outright if `terraform.workspace ==
"default"` or doesn't match that format — there is no "default" environment
for this stack, so accidentally applying to it (e.g. because nobody ran
`terraform workspace select`) would silently create a same-account clone of
the naming scheme with no intended owner, and an unrestricted name could
break AWS's own limits (e.g. the ALB name's 32-character cap) or its charset
rules (RDS, S3). Always pick or create a named workspace first — and pick the
name carefully: there's no allowlist, so a typo (`tets` vs. `test`) creates a
brand-new, permanently-tagged, billable environment instead of erroring.

This guard only covers `plan`/`apply`; Terraform does not evaluate
`lifecycle.precondition`s during `destroy`, and a `-target`ed operation can
bypass it too — both still require picking the right workspace by hand.

Each workspace's state lives at its own S3 key, via the backend's native
`workspace_key_prefix` (default `env:`): `env:/<workspace>/pocf_acuity-terraform.tfstate`.

## Naming

Every name derives from `local.name_prefix` (`pocf_acuity-<workspace>`,
underscore+hyphen — used where the resource type allows underscores) or
`local.name_prefix_hyphen` (`pocf-acuity-<workspace>`, hyphen-only — used
where it doesn't, e.g. ALB, target groups, RDS identifier, S3 bucket).
SSM parameters and CloudWatch log groups use a slash path,
`/pocf_acuity/<workspace>/...`.

Examples, for `terraform workspace select test`:

| Resource | Name |
|---|---|
| VPC | `pocf_acuity-test-vpc` |
| ALB | `pocf-acuity-test-alb` |
| RDS identifier | `pocf-acuity-test` |
| SSM parameter | `/pocf_acuity/test/db/ACUITY_PASSWORD` |
| CloudWatch log group | `/pocf_acuity/test/va-hub` |

ECS container names, Service Connect DNS aliases/`client_alias`, and the
`VAHUB_API` URL are **not** workspace-derived — they're baked into the app
images' hardcoded `http://acuity-va-*:8000` URLs and stay `acuity-*`
regardless of workspace.

The ECR repo prefix (`local.aws_ecr_repo_prefix`, in `ecr.tf`) is also frozen,
for an unrelated reason: it's owned by the image-build pipeline
(`docker/Makefile`/`docker-compose.yml`), not this Terraform root, and is out
of scope for this convention. Because it's frozen, it is **not** per-workspace
either — running `apply` in a second workspace before the first workspace's
ECR repos are decommissioned will collide with them. Don't stand up a second
long-lived workspace until that's resolved.

## Tags

Applied once, for every resource, via the `aws` provider's `default_tags` —
no resource sets its own tags on top of these:

```
Project   = "EPM-LSTR"
Stream    = "POCF_ACUITY"
Env       = terraform.workspace
Terraform = true
```
