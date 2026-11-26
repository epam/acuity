resource "aws_route53_zone" "public" {
  name          = "acuity-sandbox.epm-lstr.projects.epam.com"
  force_destroy = false

  lifecycle {
    prevent_destroy = true
  }
}

# Fetching parent domain Host Zone ID - epm-lstr.projects.epam.com
data "aws_route53_zone" "parent" {
  name = "epm-lstr.projects.epam.com"
}

# Creating NS record to link acuity hosted zone with parent domain
resource "aws_route53_record" "parent_domain_ns_record" {
  zone_id = data.aws_route53_zone.parent.id
  name    = "acuity-sandbox.epm-lstr.projects.epam.com"
  type    = "NS"
  ttl     = "3600"
  records = aws_route53_zone.public.name_servers
}
