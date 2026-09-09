# Secondary cloudWatch log groups, 14-day retention for 0.0.5-new-stack version
resource "aws_cloudwatch_log_group" "app_new_stack" {
  for_each = toset(keys(local.app_services))

  name              = "/acuity/${var.deployment}/${each.key}"
  retention_in_days = 14
}

resource "aws_cloudwatch_log_group" "flyway_new_stack" {
  name              = "/acuity/${var.deployment}/flyway"
  retention_in_days = 14
}

resource "aws_cloudwatch_log_group" "va_hub_ui_new_stack" {
  name              = "/acuity/${var.deployment}/va-hub-ui"
  retention_in_days = 14
}