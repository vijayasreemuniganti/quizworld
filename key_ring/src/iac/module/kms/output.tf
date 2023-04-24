output "key_ring" {
  value = google_kms_key_ring.my_key_ring.name  
  
}
output "crypto_key" {
    value = google_kms_crypto_key.my_crypto_key.*.name
  
}
output "kms_id" {
  value = google_kms_crypto_key.my_crypto_key.*.id
}