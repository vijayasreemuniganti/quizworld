api_key = "API_KEY"
app_key = "APP_KEY"

gcp_project_id       = "ms-gcp-g-i-cze-d-dev"
gcp_region           = "us-central1"
gcp_zone             = "us-central1-b"
gcp_credentials_path = "CREDENTIAL_FILE_PATH"
account_name      =     "datadog-integration"
display_name    =      "Datadog Integration"
pubsub_topic_name =   "export_logs_pubsub"
pubsub_subscription =  "export_logs_pubsub_sub"
duration =          "1200s"
acked_messages_pull = "true"
ack_deadline_pull  = "60"
expiration_policy_1 = "300000.5s"
retry_policy = "20s"
push_subscription = "exportlogs_to_datadog"
retention_duration = "604800s"
acked_messages_push = "true"
ack_deadline_push = "60"
end-point = "https://gcp-intake.logs.datadoghq.com/v1/input/${var.datadog_api_key}/"
log_sink = "datadog-sinks"