# ACUITY Infrastructure

Terraform for running ACUITY on AWS, one stack per environment (for example `dev-1`).

- [`terraform/bootstrap`](terraform/bootstrap/README.md): account-level resources shared by every environment. Apply it once, first.
- [`terraform/acuity`](terraform/acuity/README.md): the per-environment stack, selected by Terraform workspace.

## Infrastructure overview

![ACUITY on AWS. A VPN user reaches the internet-facing ALB on :443, :80 or :9090. The ALB routes to the ECS Fargate services va-hub-ui, va-hub and admin on :8000. va-hub and admin use RDS PostgreSQL on :5432, and admin also uses EFS. Engineers reach a bastion host through SSM Session Manager.](../docs/images/acuity-infra.png)
