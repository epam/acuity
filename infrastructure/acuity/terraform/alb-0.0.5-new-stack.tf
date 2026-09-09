# Secondary ALB for version 0.0.5
module "alb_new_stack" {
  source  = "terraform-aws-modules/alb/aws"
  version = "~> 10.0"

  name     = "acuity-${var.deployment}"
  internal = false

  vpc_id  = module.vpc.vpc_id
  subnets = module.vpc.public_subnets

  create_security_group = false
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
 