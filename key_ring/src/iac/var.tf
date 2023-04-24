variable "project" {
  type = string
}
variable "key" {
  type = string
}

variable "key_ring" {
  type    = string
}

variable "location" {
  type    = string
}

variable "crypto_key" {
  type    = list(string)
}

variable "purpose" {
  type    = string
  
}
variable "algorithm" {
  type    = string
}

variable "attestor_note" {
  type    = string
}

variable "attestor_name" {
  type    = list(string)
}
/*variable "cluster_name" {
   type    = string
}*/