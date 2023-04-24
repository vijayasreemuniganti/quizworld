variable "gcp_project_id" {
   type    = string
}

variable "gcp_region" {
   type    = string
}

variable "gcp_zone" {
   type    = string
}

variable "gcp_credentials_path" {
   type    = string

}

variable "app_key" {
  type = string
}
variable "api_key" {
  type = string
}

variable "account_name" {
   type    = string
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

variable "ack_deadline_pull" {
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

variable "ack_deadline_push" {
  type    = string
}

variable "retention_duration" {
    type    = string
}

variable "acked_messages" {
  type    = bool
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

























