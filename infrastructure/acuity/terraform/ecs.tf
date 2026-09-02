# ECS cluster `acuity` + the shared task execution role 

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

# One HTTP namespace `acuity`; services register under their exact compose name
# so the images' baked http://acuity-va-*:8000 URLs resolve with no override.
resource "aws_service_discovery_http_namespace" "acuity" {
  name = "acuity"
}

# One map drives the task defs, services and log groups. `env` holds only the
# profile var that differs per service; the identical ones are in app_common_env.
locals {
  app_common_env = {
    ENV_TYPE_PROFILE = "dev"
    AUTH_PROFILE     = "local-no-security"
    CONFIG_PROFILE   = "local-config"
  }
  app_services = {
    "va-hub"      = { cpu = 1024, memory = 2048, sg = module.sg_va_hub.id, has_lb = true, efs = false, env = { OTHER_PROFILES = "NoScheduledJobs" } }
    "admin"       = { cpu = 512, memory = 1024, sg = module.sg_admin.id, has_lb = true, efs = true, env = { STORAGE_PROFILE = "local-storage" } }
    "va-security" = { cpu = 512, memory = 1024, sg = module.sg_va_security.id, has_lb = false, efs = false, env = { OTHER_PROFILES = "default,postgres-mode" } }
  }
}

resource "aws_ecs_task_definition" "app" {
  for_each = local.app_services

  family                   = "acuity-${each.key}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = each.value.cpu
  memory                   = each.value.memory
  execution_role_arn       = aws_iam_role.ecs_execution.arn
  # No task role: these tasks only reach RDS and the Service Connect peers.

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64"
  }

  # Admin gets a durable EFS-backed volume; the others don't.
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
      image = "${module.ecr[each.key].repository_url}:${var.image_tag}"

      # Migrations run to completion before the app starts; a stuck migration
      # fails the task (and the deploy) at startTimeout rather than hanging.
      dependsOn = [
        { containerName = "flyway", condition = "SUCCESS" },
      ]
      startTimeout = 600

      portMappings = [
        { containerPort = 8000, name = "acuity-${each.key}", appProtocol = "http" },
      ]

      # Admin only - the EFS volume at local-storage.path as shipped.
      mountPoints = each.value.efs ? [{
        sourceVolume  = "admin-storage"
        containerPath = "/usr/root/local-file-storage"
      }] : []

      # No VASECURITY_URL / VAHUB_URL - the image defaults are what Service Connect resolves.
      # POSTGRES_URL is the only URL override.
      environment = [
        for k, v in merge(local.app_common_env, {
          POSTGRES_USER = "acuity"
          POSTGRES_URL  = "jdbc:postgresql://${module.rds.db_instance_address}:5432/acuity_db"
          JAVA_OPTIONS  = "-XX:MaxRAMPercentage=60"
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
      image = "${module.ecr["flyway"].repository_url}:${var.image_tag}"

      # Non-essential: exits 0 after migrating, then the app container starts.
      # essential = false is required - ECS rejects a SUCCESS dependency on an essential container.
      essential = false

      # Let an in-flight migration finish (or fail cleanly) if the deploy is aborted.
      stopTimeout = 120

      # No command/entrypoint override - the image bakes CMD.
      # LOCK_RETRY_COUNT=-1: the sidecars on the other DB services wait on the
      # schema-history lock instead of erroring out while the first one migrates.
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

  name            = "acuity-${each.key}"
  cluster         = module.ecs_cluster.arn
  task_definition = aws_ecs_task_definition.app[each.key].arn
  desired_count   = 1
  launch_type     = "FARGATE"

  health_check_grace_period_seconds = each.value.has_lb ? 300 : null

  network_configuration {
    subnets         = module.vpc.public_subnets
    security_groups = [each.value.sg]
    # Public subnets, no NAT. The task SG is the only guard.
    # Upgrade path: private subnets + NAT / VPC endpoints.
    assign_public_ip = true
  }

  # ECS registers the task IP into the ALB target group named after this service
  # (target_type = "ip"). va-security has none.
  dynamic "load_balancer" {
    for_each = each.value.has_lb ? [1] : []
    content {
      target_group_arn = module.alb.target_groups[each.key].arn
      container_name   = "acuity-${each.key}"
      container_port   = local.app_port
    }
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

  # A failed rollout (e.g. a bad migration in the flyway sidecar) rolls this
  # service back to the last healthy revision; running tasks keep serving.
  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  # `apply` blocks on the rollout, so a failed migration fails the apply.
  wait_for_steady_state = true
}

# va-hub-ui (nginx SPA) - standalone, NOT a Service Connect member.
resource "aws_ecs_task_definition" "va_hub_ui" {
  family                   = "acuity-va-hub-ui"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.ecs_execution.arn
  # No task role: the container only serves static files and (on Docker) proxies.

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64" # AD-1 linux/amd64 - see deferred-work.md
  }

  container_definitions = jsonencode([
    {
      name  = "acuity-va-hub-ui"
      image = "${module.ecr["va-hub-ui"].repository_url}:${var.image_tag}"

      # No `name` - va-hub-ui is not on Service Connect.
      portMappings = [
        { containerPort = local.app_port },
      ]

      # Dead on AWS (the ALB /resources/* rule intercepts before nginx), but the
      # image's nginx.conf.template runs `envsubst` at boot and an empty
      # ${VAHUB_API} yields an invalid proxy_pass - so pass the compose default.
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
  name            = "acuity-va-hub-ui"
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
