output "snapshot_name" {
  description = "snapshot name value"
  value       = google_compute_snapshot.snapshot.name
}
output "snapshot_self_link" {
  value = google_compute_snapshot.snapshot.self_link
}