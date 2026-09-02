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

# --- Story 1.5: the three backend app services on ECS Service Connect (AD-1, AD-3, AD-7) ---

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
  # `has_lb` = this service is behind the ALB (its target group key == the map
  # key). va-security has no ALB path. Story 1.6 / AD-4.
  app_services = {
    "va-hub"      = { cpu = 1024, memory = 2048, sg = module.sg_va_hub.id, has_lb = true, env = { OTHER_PROFILES = "NoScheduledJobs" } }
    "admin"       = { cpu = 512, memory = 1024, sg = module.sg_admin.id, has_lb = true, env = { STORAGE_PROFILE = "local-storage" } }
    "va-security" = { cpu = 512, memory = 1024, sg = module.sg_va_security.id, has_lb = false, env = { OTHER_PROFILES = "default,postgres-mode" } }
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

  # AD-1: pin linux/amd64 explicitly. The release images must actually be built
  # amd64 (see deferred-work.md - the Makefile sets no --platform).
  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64"
  }

  container_definitions = jsonencode([
    {
      name  = "acuity-${each.key}"
      image = "${module.ecr[each.key].repository_url}:${var.image_tag}"

      portMappings = [
        { containerPort = 8000, name = "acuity-${each.key}", appProtocol = "http" },
      ]

      # No VASECURITY_URL / VAHUB_URL - the image defaults are what Service
      # Connect resolves (AD-3). POSTGRES_URL is the only URL override.
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

  # AD-1: grace period only where there's a load balancer to health-check against;
  # va-security has none, so it stays unset there (deferred-work.md gap).
  health_check_grace_period_seconds = each.value.has_lb ? 300 : null

  network_configuration {
    subnets         = module.vpc.public_subnets
    security_groups = [each.value.sg]
    # Public subnets, no NAT (AD-6); the task SG is the only guard. Upgrade
    # path: private subnets + NAT / VPC endpoints.
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

  # AD-7: task definition revisions are managed out of band (redeploy on a new
  # image_tag), not by Terraform diffing the container def.
  lifecycle {
    ignore_changes = [task_definition]
  }
}

# --- Story 1.6: va-hub-ui (nginx SPA) - standalone, NOT a Service Connect member ---
# It has no DB and a different env contract, so it stays out of `local.app_services`
# (folding it in would mean a conditional on every field). AD-1: 0.25 vCPU / 0.5 GB.

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

  # AD-7: revisions managed out of band, same as the app services.
  lifecycle {
    ignore_changes = [task_definition]
  }
}
