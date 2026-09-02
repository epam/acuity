# ECS cluster `acuity` + the shared task execution role (Story 1.5's app tasks
# reuse it), and the one-off `acuity-flyway` migration task (AD-1, AD-7, AD-8).

module "ecs_cluster" {
  source  = "terraform-aws-modules/ecs/aws//modules/cluster"
  version = "~> 6.0"

  name = "acuity"

  setting = [
    { name = "containerInsights", value = "disabled" }, # NFR3
  ]

  # The module's default execute_command_configuration shows a "placeholder"
  # log group in the plan - harmless, nothing in this PoC uses ECS Exec.
  create_cloudwatch_log_group = false
  create_task_exec_iam_role   = false
}

resource "aws_iam_role" "ecs_execution" {
  name = "acuity-poc-ecs-execution"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Action    = "sts:AssumeRole"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_execution_managed" {
  role       = aws_iam_role.ecs_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy_attachment" "ecs_execution_ssm_secrets" {
  role       = aws_iam_role.ecs_execution.name
  policy_arn = aws_iam_policy.ssm_secrets_read.arn
}

resource "aws_ecs_task_definition" "flyway" {
  family                   = "acuity-flyway"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.ecs_execution.arn
  # No task role: the container only talks to RDS, no AWS API access.

  container_definitions = jsonencode([
    {
      name  = "flyway"
      image = "${module.ecr["flyway"].repository_url}:${var.image_tag}"

      # No command/entrypoint override - the image bakes
      # sh -c 'flyway "-placeholders.user.acuity.password=${FLYWAY_ACUITY_PASSWORD}" migrate'.
      environment = [
        { name = "FLYWAY_URL", value = "jdbc:postgresql://${module.rds.db_instance_address}:5432/acuity_db" },
        { name = "FLYWAY_USER", value = "dbadmin" },
      ]

      secrets = [
        { name = "FLYWAY_PASSWORD", valueFrom = aws_ssm_parameter.dbadmin_password.arn },
        { name = "FLYWAY_ACUITY_PASSWORD", valueFrom = aws_ssm_parameter.acuity_password.arn },
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.flyway.name
          "awslogs-region"        = data.aws_region.current.region
          "awslogs-stream-prefix" = "flyway" # required for the FARGATE awslogs driver
        }
      }
    }
  ])
}
