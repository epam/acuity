# CloudWatch log groups, 14-day retention 
resource "aws_cloudwatch_log_group" "flyway" {
  name              = "/acuity/poc/flyway"
  retention_in_days = 14
}

resource "aws_cloudwatch_log_group" "app" {
  for_each = toset(keys(local.app_services))

  name              = "/acuity/poc/${each.key}"
  retention_in_days = 14
}

resource "aws_cloudwatch_log_group" "va_hub_ui" {
  name              = "/acuity/poc/va-hub-ui"
  retention_in_days = 14
}
