# Zone lives in terraform/bootstrap (shared across workspaces); read by name here.
data "aws_route53_zone" "public" {
  name         = "acuity-sandbox.epm-lstr.projects.epam.com"
  private_zone = false
}

# Wildcard cert is issued in terraform/bootstrap; read by domain here.
data "aws_acm_certificate" "wildcard" {
  domain   = "*.${data.aws_route53_zone.public.name}"
  statuses = ["ISSUED"]
}

# <workspace>.<zone> -> ALB
resource "aws_route53_record" "alb" {
  zone_id = data.aws_route53_zone.public.zone_id
  name    = "${terraform.workspace}.${data.aws_route53_zone.public.name}"
  type    = "A"

  alias {
    name                   = module.alb.dns_name
    zone_id                = module.alb.zone_id
    evaluate_target_health = true
  }
}
