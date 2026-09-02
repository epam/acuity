variable "vpn_cidrs" {
  description = "Corporate VPN egress CIDR(s) allowed to reach the ALB. No default on purpose - must be supplied via terraform.tfvars or -var."
  type        = list(string)

  validation {
    condition     = length(var.vpn_cidrs) > 0
    error_message = "vpn_cidrs must not be empty - the ALB has no other ingress path."
  }

  validation {
    condition     = alltrue([for c in var.vpn_cidrs : can(cidrhost(c, 0))])
    error_message = "Every entry in vpn_cidrs must be a syntactically valid CIDR, e.g. \"203.0.113.5/32\"."
  }

  validation {
    # /24 or smaller only - also rejects 0.0.0.0/0 (prefix 0) specifically.
    condition     = alltrue([for c in var.vpn_cidrs : try(tonumber(element(split("/", c), 1)), 0) >= 24])
    error_message = "Every entry in vpn_cidrs must be /24 or smaller (a single host or small block) - 0.0.0.0/0 or anything broader defeats the VPN-only restriction."
  }
}
