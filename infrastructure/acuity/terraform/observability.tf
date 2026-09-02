# CloudWatch log groups, 14-day retention (AD-13). Flyway (1.4) and the three
# backend services (1.5); Story 1.9 adds va-hub-ui's group and the AWS Budget.

resource "aws_cloudwatch_log_group" "flyway" {
  name              = "/acuity/poc/flyway"
  retention_in_days = 14
}

resource "aws_cloudwatch_log_group" "app" {
  for_each = toset(keys(local.app_services))

  name              = "/acuity/poc/${each.key}"
  retention_in_days = 14
}

# Story 1.6: va-hub-ui is a standalone service (not a `local.app_services` member).
resource "aws_cloudwatch_log_group" "va_hub_ui" {
  name              = "/acuity/poc/va-hub-ui"
  retention_in_days = 14
}
