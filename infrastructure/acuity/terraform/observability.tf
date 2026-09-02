# CloudWatch log groups (AD-13, 14-day retention). Flyway's group is here
# because Story 1.4 needs it; Story 1.9 adds the 4 app groups + the AWS Budget.

resource "aws_cloudwatch_log_group" "flyway" {
  name              = "/acuity/poc/flyway"
  retention_in_days = 14
}
