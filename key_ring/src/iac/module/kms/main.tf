resource "google_kms_key_ring" "my_key_ring" {
  name     =  var.key_ring 
  location =  var.location 
}

resource "google_kms_crypto_key" "my_crypto_key" {
  count    = length(var.crypto_key)
  name     = var.crypto_key[count.index]
  key_ring = google_kms_key_ring.my_key_ring.id
  purpose  = var.purpose
  version_template {
    algorithm = var.algorithm 
  }
}