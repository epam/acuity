# ECS cluster and shared task execution role.

module "ecs_cluster" {
  source  = "terraform-aws-modules/ecs/aws//modules/cluster"
  version = "~> 6.0"

  name = local.name_prefix

  setting = [
    { name = "containerInsights", value = "disabled" }, # NFR3
  ]

  # Default execute_command_configuration adds a harmless placeholder log group; ECS Exec unused.
  create_cloudwatch_log_group = false
  create_task_exec_iam_role   = false
}

resource "aws_iam_role" "ecs_execution" {
  name = "${local.name_prefix}-ecs-execution"
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

# Service Connect namespace; exact compose names let baked http://acuity-va-*:8000 URLs resolve.
resource "aws_service_discovery_http_namespace" "acuity" {
  name = local.name_prefix
}

# One map drives task defs, services and log groups; shared env is in app_common_env.
locals {
  app_common_env = {
    ENV_TYPE_PROFILE = "dev"
    AUTH_PROFILE     = "local-no-security"
    CONFIG_PROFILE   = "local-config"
  }
  app_services = {
    "va-hub" = { cpu = 1024, memory = 2048, sg = module.sg_va_hub.id, efs = false, env = { OTHER_PROFILES = "NoScheduledJobs" } }
    "admin"  = { cpu = 512, memory = 1024, sg = module.sg_admin.id, efs = true, env = { STORAGE_PROFILE = "local-storage" } }
  }
}

resource "aws_ecs_task_definition" "app" {
  for_each = local.app_services

  family                   = "${local.name_prefix}-${each.key}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = each.value.cpu
  memory                   = each.value.memory
  execution_role_arn       = aws_iam_role.ecs_execution.arn
  # No task role: tasks only reach RDS and Service Connect peers.

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64"
  }

  dynamic "volume" {
    for_each = each.value.efs ? [1] : []
    content {
      name = "admin-storage"
      efs_volume_configuration {
        file_system_id     = module.efs.id
        transit_encryption = "ENABLED"
      }
    }
  }

  container_definitions = jsonencode([
    {
      name  = "acuity-${each.key}"
      image = "${data.aws_ecr_repository.app[each.key].repository_url}:${var.image_tag}"

      # Wait for migrations; a stuck one fails the deploy at startTimeout.
      dependsOn = [
        { containerName = "flyway", condition = "SUCCESS" },
      ]
      startTimeout = 600

      portMappings = [
        { containerPort = 8000, name = "acuity-${each.key}", appProtocol = "http" },
      ]

      mountPoints = each.value.efs ? [{
        sourceVolume  = "admin-storage"
        containerPath = "/usr/root/local-file-storage"
      }] : []

      environment = [
        for k, v in merge(local.app_common_env, {
          POSTGRES_USER = "acuity"
          POSTGRES_URL  = "jdbc:postgresql://${module.rds.db_instance_address}:5432/acuity_db"
          # MaxRAMPercentage needs a float ("=60" is rejected).
          # Explicit against base-image changes.
          JAVA_OPTIONS = join(" ", [
            "-XX:+UseContainerSupport",
            "-XX:MaxRAMPercentage=60.0",
            "-Dspring.cloud.config.enabled=false",
            "-Dspring.main.allow-bean-definition-overriding=true",
            "-Dspring.main.allow-circular-references=true",
            "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED",
          ])
        }, each.value.env) : { name = k, value = v }
      ]

      secrets = [
        { name = "POSTGRES_PASSWORD", valueFrom = aws_ssm_parameter.acuity_password.arn },
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.app[each.key].name
          "awslogs-region"        = data.aws_region.current.region
          "awslogs-stream-prefix" = each.key # required for the FARGATE awslogs driver
        }
      }
    },
    {
      name  = "flyway"
      image = "${data.aws_ecr_repository.app["flyway"].repository_url}:${var.image_tag}"

      # essential = false: ECS rejects a SUCCESS dependency on an essential container.
      essential = false

      stopTimeout = 120

      # -1: other services' sidecars wait on the schema-history lock instead of erroring.
      environment = [
        { name = "FLYWAY_URL", value = "jdbc:postgresql://${module.rds.db_instance_address}:5432/acuity_db" },
        { name = "FLYWAY_USER", value = "dbadmin" },
        { name = "FLYWAY_LOCK_RETRY_COUNT", value = "-1" },
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
          "awslogs-stream-prefix" = each.key # required for the FARGATE awslogs driver
        }
      }
    }
  ])
}

resource "aws_ecs_service" "app" {
  for_each = local.app_services

  name            = "${local.name_prefix}-${each.key}"
  cluster         = module.ecs_cluster.arn
  task_definition = aws_ecs_task_definition.app[each.key].arn
  desired_count   = 1
  launch_type     = "FARGATE"

  health_check_grace_period_seconds = 300

  network_configuration {
    subnets         = module.vpc.public_subnets
    security_groups = [each.value.sg]
    # Public subnets, no NAT; task SG is the only guard. Upgrade: private subnets + NAT/endpoints.
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = module.alb.target_groups[each.key].arn
    container_name   = "acuity-${each.key}"
    container_port   = local.app_port
  }

  service_connect_configuration {
    enabled   = true
    namespace = aws_service_discovery_http_namespace.acuity.arn

    service {
      port_name = "acuity-${each.key}"

      client_alias {
        dns_name = "acuity-${each.key}"
        port     = 8000
      }
    }
  }

  # Failed rollout (e.g. bad migration) rolls back to the last healthy revision.
  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  wait_for_steady_state = true
}

# va-hub-ui (nginx SPA): not on Service Connect.
resource "aws_ecs_task_definition" "va_hub_ui" {
  family                   = "${local.name_prefix}-va-hub-ui"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.ecs_execution.arn

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64" # AD-1 linux/amd64 - see deferred-work.md
  }

  container_definitions = jsonencode([
    {
      name  = "acuity-va-hub-ui"
      image = "${data.aws_ecr_repository.app["va-hub-ui"].repository_url}:${var.image_tag}"

      portMappings = [
        { containerPort = local.app_port },
      ]

      # Unused on AWS (ALB rule intercepts), but nginx envsubst breaks on an empty value; use the compose default.
      environment = [
        { name = "VAHUB_API", value = "http://acuity-va-hub:8000" },
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.va_hub_ui.name
          "awslogs-region"        = data.aws_region.current.region
          "awslogs-stream-prefix" = "va-hub-ui" # required for the FARGATE awslogs driver
        }
      }
    }
  ])
}

resource "aws_ecs_service" "va_hub_ui" {
  name            = "${local.name_prefix}-va-hub-ui"
  cluster         = module.ecs_cluster.arn
  task_definition = aws_ecs_task_definition.va_hub_ui.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  health_check_grace_period_seconds = 300 # AD-1

  network_configuration {
    subnets          = module.vpc.public_subnets
    security_groups  = [module.sg_va_hub_ui.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = module.alb.target_groups["va-hub-ui"].arn
    container_name   = "acuity-va-hub-ui"
    container_port   = local.app_port
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  wait_for_steady_state = true
}
