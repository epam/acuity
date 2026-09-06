# Bastion for reaching RDS and EFS. 
# SSM Session Manager only - no SSH key. 

data "aws_ami" "al2023_arm64" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-arm64"]
  }

  filter {
    name   = "architecture"
    values = ["arm64"]
  }
}

resource "aws_iam_role" "bastion" {
  name = "acuity-poc-bastion"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Action    = "sts:AssumeRole"
      Principal = { Service = "ec2.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "bastion_ssm_core" {
  role       = aws_iam_role.bastion.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "bastion" {
  name = "acuity-poc-bastion"
  role = aws_iam_role.bastion.name
}

resource "aws_instance" "bastion" {
  ami                         = data.aws_ami.al2023_arm64.id
  instance_type               = "t4g.micro"
  subnet_id                   = module.vpc.public_subnets[0]
  vpc_security_group_ids      = [module.sg_bastion.id]
  iam_instance_profile        = aws_iam_instance_profile.bastion.name
  associate_public_ip_address = true


  root_block_device {
    volume_type = "gp3"
    volume_size = 30
  }

  user_data = <<-EOF
  #!/bin/bash
  set -euxo pipefail
  dnf install -y amazon-efs-utils
  mkdir -p /usr/root/local-file-storage
  mountpoint -q /usr/root/local-file-storage || mount -t efs -o tls ${module.efs.id}:/ /usr/root/local-file-storage
  grep -q ${module.efs.id} /etc/fstab || echo "${module.efs.id}:/ /usr/root/local-file-storage efs _netdev,tls 0 0" >> /etc/fstab
  chmod 777 /usr/root/local-file-storage
  EOF

  user_data_replace_on_change = true
  force_destroy               = true

  tags       = { Name = "acuity-poc-bastion" }
  depends_on = [module.efs]
}

resource "aws_iam_role" "bastion_access" {
  name = "acuity-poc-bastion-access"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Action    = "sts:AssumeRole"
      Principal = { AWS = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root" }
    }]
  })
}

data "aws_iam_policy_document" "bastion_ssm_session" {
  statement {
    sid     = "StartSessionFromVpnOnly"
    actions = ["ssm:StartSession"]
    resources = [
      aws_instance.bastion.arn,
      "arn:aws:ssm:${data.aws_region.current.region}::document/AWS-StartPortForwardingSessionToRemoteHost",
      "arn:aws:ssm:${data.aws_region.current.region}::document/SSM-SessionManagerRunShell",
    ]

    condition {
      test     = "IpAddress"
      variable = "aws:SourceIp"
      values   = var.vpn_cidrs
    }
  }

  statement {
    sid       = "ManageOwnSession"
    actions   = ["ssm:TerminateSession", "ssm:ResumeSession"]
    resources = ["arn:aws:ssm:${data.aws_region.current.region}:${data.aws_caller_identity.current.account_id}:session/$${aws:userid}-*"]
  }
}

resource "aws_iam_role_policy" "bastion_ssm_session" {
  name   = "acuity-poc-bastion-ssm-session"
  role   = aws_iam_role.bastion_access.id
  policy = data.aws_iam_policy_document.bastion_ssm_session.json
}

output "bastion_instance_id" {
  description = "SSM target ID for `aws ssm start-session --target <id>`."
  value       = aws_instance.bastion.id
}

output "bastion_access_role_arn" {
  description = "Assume this role (from a VPN-connected client) before starting a session."
  value       = aws_iam_role.bastion_access.arn
}
