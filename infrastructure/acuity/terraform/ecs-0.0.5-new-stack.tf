# Secondary deployment cluster + services for the new-stack image.
# Reuses: execution role, VPC, subnets, security groups, RDS, EFS, SSM.
# New: cluster, task definitions, service discovery namespace, ECS services, log groups, ALB (see alb-new-stack.tf).


module "ecs_cluster_new_stack" {
  source  = "terraform-aws-modules/ecs/aws//modules/cluster"
  version = "~> 6.0"

  name = "acuity-${var.deployment}"

  setting = [
    { name = "containerInsights", value = "disabled" },
  ]

  create_cloudwatch_log_group = false
  create_task_exec_iam_role   = false
}

resource "aws_iam_role" "ecs_execution_v005" {
  name = "acuity-${var.deployment}-ecs-execution"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Action    = "sts:AssumeRole"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_execution_v005_managed" {
  role       = aws_iam_role.ecs_execution_v005.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy_attachment" "ecs_execution_v005_ssm_secrets" {
  role       = aws_iam_role.ecs_execution_v005.name
  policy_arn = aws_iam_policy.ssm_secrets_read_v005.arn
}

resource "aws_service_discovery_http_namespace" "acuity_new_stack" {
  name = "acuity-${var.deployment}"
}

resource "aws_ecs_task_definition" "app_new_stack" {
  for_each = local.app_services

  family                   = "acuity-${var.deployment}-${each.key}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = each.value.cpu
  memory                   = each.value.memory
  execution_role_arn       = aws_iam_role.ecs_execution_v005.arn

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
      image = "${module.ecr[each.key].repository_url}:${var.new_stack_image_tag}"

      dependsOn    = [{ containerName = "flyway", condition = "SUCCESS" }]
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
          POSTGRES_URL  = "jdbc:postgresql://${module.rds_new_stack.db_instance_address}:5432/acuity_db"
          JAVA_OPTIONS  = "-XX:+UseContainerSupport -XX:MaxRAMPercentage=60.0"
        }, each.value.env) : { name = k, value = v }
      ]

      secrets = [
        { name = "POSTGRES_PASSWORD", valueFrom = aws_ssm_parameter.acuity_password_v005.arn },
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/acuity/${var.deployment}/${each.key}"
          "awslogs-region"        = data.aws_region.current.region
          "awslogs-stream-prefix" = each.key
        }
      }
    },
    {
      name      = "flyway"
      image     = "${module.ecr["flyway"].repository_url}:${var.new_stack_image_tag}"
      essential = false

      stopTimeout = 120

      environment = [
        { name = "FLYWAY_URL", value = "jdbc:postgresql://${module.rds_new_stack.db_instance_address}:5432/acuity_db" },
        { name = "FLYWAY_USER", value = "dbadmin" },
        { name = "FLYWAY_LOCK_RETRY_COUNT", value = "-1" },
      ]

      secrets = [
        { name = "FLYWAY_PASSWORD", valueFrom = aws_ssm_parameter.dbadmin_password_v005.arn },
        { name = "FLYWAY_ACUITY_PASSWORD", valueFrom = aws_ssm_parameter.acuity_password_v005.arn },
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/acuity/${var.deployment}/flyway"
          "awslogs-region"        = data.aws_region.current.region
          "awslogs-stream-prefix" = each.key
        }
      }
    }
  ])
}

resource "aws_ecs_task_definition" "va_hub_ui_new_stack" {
  family                   = "acuity-${var.deployment}-va-hub-ui"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.ecs_execution_v005.arn

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64"
  }

  container_definitions = jsonencode([
    {
      name  = "acuity-va-hub-ui"
      image = "${module.ecr["va-hub-ui"].repository_url}:${var.new_stack_image_tag}"

      portMappings = [
        { containerPort = local.app_port },
      ]

      environment = [
        { name = "VAHUB_API", value = "http://acuity-va-hub:8000" },
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/acuity/${var.deployment}/va-hub-ui"
          "awslogs-region"        = data.aws_region.current.region
          "awslogs-stream-prefix" = "va-hub-ui"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "app_new_stack" {
  depends_on = [module.rds_new_stack]
  for_each = local.app_services

  name            = "acuity-${var.deployment}-${each.key}"
  cluster         = module.ecs_cluster_new_stack.arn
  task_definition = aws_ecs_task_definition.app_new_stack[each.key].arn
  desired_count   = 1
  launch_type     = "FARGATE"

  health_check_grace_period_seconds = each.value.has_lb ? 300 : null

  network_configuration {
    subnets          = module.vpc.public_subnets
    security_groups  = [each.value.sg]
    assign_public_ip = true
  }

  dynamic "load_balancer" {
    for_each = each.value.has_lb ? [1] : []
    content {
      target_group_arn = module.alb_new_stack.target_groups[each.key].arn
      container_name   = "acuity-${each.key}"
      container_port   = local.app_port
    }
  }

  service_connect_configuration {
    enabled   = true
    namespace = aws_service_discovery_http_namespace.acuity_new_stack.arn

    service {
      port_name = "acuity-${each.key}"

      client_alias {
        dns_name = "acuity-${each.key}"
        port     = 8000
      }
    }
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  wait_for_steady_state = true
}

resource "aws_ecs_service" "va_hub_ui_new_stack" {
  name            = "acuity-${var.deployment}-va-hub-ui"
  cluster         = module.ecs_cluster_new_stack.arn
  task_definition = aws_ecs_task_definition.va_hub_ui_new_stack.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  health_check_grace_period_seconds = 300

  network_configuration {
    subnets          = module.vpc.public_subnets
    security_groups  = [module.sg_va_hub_ui.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = module.alb_new_stack.target_groups["va-hub-ui"].arn
    container_name   = "acuity-va-hub-ui"
    container_port   = local.app_port
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  wait_for_steady_state = true
}
