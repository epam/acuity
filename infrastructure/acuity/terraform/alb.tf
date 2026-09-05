# The one internet-facing edge. No ingress rule here: `sg_alb` already gates :80 / :9090 to `var.vpn_cidrs`.

locals {
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

  create_security_group = false # `sg_alb` is the sole guard. The module must not create its own.
  security_groups       = [module.sg_alb.id]

  enable_deletion_protection = false

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
    "admin-9090" = {
      port     = 9090
      protocol = "HTTP"
      forward  = { target_group_key = "admin" }
    }
  }
}
