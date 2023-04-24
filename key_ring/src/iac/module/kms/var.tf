variable "project" {
  type = string
}

variable "key_ring" {
  type    = string
}

variable "location" {
  type    = string
  default = "us-central1"
}

variable "crypto_key" {
  type    = list(string)
}

variable "purpose" {
  type    = string
  default = "ASYMMETRIC_SIGN"
}
variable "algorithm" {
  type    = string
  default = "EC_SIGN_P256_SHA256"
}