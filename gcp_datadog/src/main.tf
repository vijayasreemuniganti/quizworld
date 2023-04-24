module "export_logs_datadog" {
  source      = "./modules/pubsub_datadog/"
  project     = var.gcp_project_id
  account_name  = var.account_name 
  display_name =  var.display_name
  pubsub_topic_name = var. pubsub_topic_name
  pubsub_subscription = var.pubsub_subscription
  duration = var.duration
  ack_deadline_pull = var.ack_deadline_pull
  expiration_policy_1 = var.expiration_policy_1
  retry_policy = var.retry_policy
  push_subscription = var.push_subscription
ack_deadline_push =var.ack_deadline_push
retention_duration = var.retention_duration
acked_messages = var.acked_messages
   
}
