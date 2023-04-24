output "attestor_name" {
    value = google_binary_authorization_attestor.attestor.*.name
  
}