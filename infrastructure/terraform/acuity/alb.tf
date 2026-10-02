# Internet-facing ALB; ingress is gated by sg_alb (var.vpn_cidrs).

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

  name     = "${local.name_prefix_hyphen}-alb"
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
    "https-443" = {
      port            = 443
      protocol        = "HTTPS"
      certificate_arn = data.aws_acm_certificate.wildcard.arn
      ssl_policy      = "ELBSecurityPolicy-TLS13-1-2-2021-06" # module default is TLS 1.3-only; keep 1.2 for older clients/proxies
      forward         = { target_group_key = "va-hub-ui" }
      rules = {
        "resources" = {
          priority   = 1
          actions    = [{ type = "forward", forward = { target_group_key = "va-hub" } }]
          conditions = [{ path_pattern = { values = ["/resources/*"] } }]
        }
      }
    }
    "admin-9090" = {
      port            = 9090
      protocol        = "HTTPS"
      certificate_arn = data.aws_acm_certificate.wildcard.arn
      ssl_policy      = "ELBSecurityPolicy-TLS13-1-2-2021-06" # keep in sync with https-443
      forward         = { target_group_key = "admin" }
    }
  }
}
