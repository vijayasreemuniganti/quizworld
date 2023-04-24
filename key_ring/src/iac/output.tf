output "key_ring" {
  value = module.kms_keyring.key_ring 
  
}
output "crypto_key" {
    value = module.kms_keyring.crypto_key
  
}
output "kms_id" {
  value= module.kms_keyring.kms_id
}
output "attestor_name" {
  value = module.attestor_creation.attestor_name
}
