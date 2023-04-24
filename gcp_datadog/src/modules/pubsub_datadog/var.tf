variable "account_name" {
   type    = string
}


variable "app_key" {
  type = string
}
variable "api_key" {
  type = string
}
variable "display_name" {
  type    = string
}

variable "pubsub_topic_name" {
  type    = string
}


variable "pubsub_subscription" {
  type    = string
}

variable "duration" {
  type    = string
}

variable "ack_deadline" {
  type    = string
}
variable "expiration_policy_1"{

    type = number
}
variable "retry_policy"{
    type = string
}

variable "push_subscription" {
  type = string
}

variable "retention_duration" {
    type    = string
}

variable "acked_messages" {
  type    = string
}
variable "expiration_policy_2"{

    type = number
}
variable "end-point" {
  type  = string
}

variable "log_sink" {
  type = string
}

























