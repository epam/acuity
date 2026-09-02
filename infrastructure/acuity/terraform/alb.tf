# Story 1.6 - the one internet-facing edge. No ingress rule here: `sg_alb`
# (Story 1.1) already gates :80 / :9090 to `var.vpn_cidrs`.

locals {
  # Per-target-group health check; every other TG field is shared (below).
  tg_health = {
    "va-hub-ui" = { path = "/", matcher = "200-399" }
    "va-hub"    = { path = "/resources/", matcher = "200-499" }
    "admin"     = { path = "/", matcher = "200-399" }
  }
}

module "alb" {
  source  = "terraform-aws-modules/alb/aws"
  version = "~> 10.0"

  name     = "acuity-poc"
  internal = false

  vpc_id  = module.vpc.vpc_id
  subnets = module.vpc.public_subnets

  # `sg_alb` is the sole guard (AD-5); the module must not create its own.
  create_security_group = false
  security_groups       = [module.sg_alb.id]

  enable_deletion_protection = false

  # ECS registers task IPs; Terraform manages no attachment (create_attachment = false).
  target_groups = {
    for k, hc in local.tg_health : k => {
      protocol          = "HTTP"
      port              = local.app_port
      target_type       = "ip"
      create_attachment = false
      health_check      = hc
    }
  }

  listeners = {
    # :80 - SPA by default, /resources/* peeled off to va-hub before nginx sees it.
    "http-80" = {
      port     = 80
      protocol = "HTTP"
      forward  = { target_group_key = "va-hub-ui" }
      rules = {
        "resources" = {
          priority   = 1
          actions    = [{ type = "forward", forward = { target_group_key = "va-hub" } }]
          conditions = [{ path_pattern = { values = ["/resources/*"] } }]
        }
      }
    }
    # :9090 - admin serves at root, no context path (AD-4).
    "admin-9090" = {
      port     = 9090
      protocol = "HTTP"
      forward  = { target_group_key = "admin" }
    }
  }
}
