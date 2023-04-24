resource "google_service_account" "datadog" {
  account_id   = var.account_name
  display_name = var.display_name
}

resource "google_project_iam_member" "datadog-connect" {
  for_each = toset([
    "roles/cloudasset.viewer",
    "roles/compute.viewer",
    "roles/monitoring.viewer",
  ])
  role   = each.key
  member = "serviceAccount:${google_service_account.datadog.email}"
}

resource "google_service_account_key" "datadog" {
  service_account_id = google_service_account.datadog.name
}

resource "datadog_integration_gcp" "awesome_gcp_project_integration" {
  project_id     = jsondecode(base64decode(google_service_account_key.datadog.private_key))["project_id"]
  private_key    = jsondecode(base64decode(google_service_account_key.datadog.private_key))["private_key"]
  private_key_id = jsondecode(base64decode(google_service_account_key.datadog.private_key))["private_key_id"]
  client_email   = jsondecode(base64decode(google_service_account_key.datadog.private_key))["client_email"]
  client_id      = jsondecode(base64decode(google_service_account_key.datadog.private_key))["client_id"]
}



resource "google_pubsub_topic" "export_logs_to_datadog" {
  name = var.pubsub_topic_name
}

resource "google_pubsub_subscription" "pull_subscription" {
  name  = var.pubsub_subscription
  topic = google_pubsub_topic.export_logs_to_datadog.name

  # 20 minutes
  message_retention_duration = var.duration
  retain_acked_messages      = var.acked_messages_pull

  ack_deadline_seconds = var.ack_deadline_pull

  expiration_policy {
    ttl = var.expiration_policy_1
  }
  retry_policy {
    minimum_backoff = var.retry_policy
  }

  enable_message_ordering    = false
}

resource "google_pubsub_subscription" "datadog-logs" {
  name  = var.push_subscription
  topic = google_pubsub_topic.export_logs_to_datadog.name

  message_retention_duration = var.retention_duration
  retain_acked_messages      = var.acked_messages_push
  ack_deadline_seconds       = var.ack_deadline_push

  push_config {
    push_endpoint = var.end_point
} 
}

resource "google_logging_project_sink" "datadog-sink" {
  name                   = var.log_sink
  destination            = "pubsub.googleapis.com/${google_pubsub_topic.export_logs_to_datadog.id}"
  //filter                 = "resource.type = gce_instance AND severity >= WARNING"
  unique_writer_identity = false
}


resource "google_project_iam_member" "pubsub-publisher-permisson" {
  role   = "roles/pubsub.publisher"
  member = google_logging_project_sink.datadog-sink.writer_identity
}











