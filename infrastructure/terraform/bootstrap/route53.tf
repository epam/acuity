resource "aws_route53_zone" "public" {
  name          = "acuity-sandbox.epm-lstr.projects.epam.com"
  force_destroy = false

  lifecycle {
    prevent_destroy = true
  }
}
