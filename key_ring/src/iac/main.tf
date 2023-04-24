module "kms_keyring" {
  source      = "./module/kms/"
  project     = var.project
  key_ring    = var.key_ring
  location    = var.location 
  crypto_key  = var.crypto_key
  purpose     = var.purpose
  algorithm   = var.algorithm 
   
}

module "attestor_creation" {
  depends_on = [module.kms_keyring]
  source  = "./module/attestor/"
  attestor_note = var.attestor_note
  attestor_name = var.attestor_name
  crypto_key_id = module.kms_keyring.kms_id[0]

}

module "binary_authorizatioln" {
  depends_on = [module.attestor_creation]
  source  = "./module/bin_auth/"
 // cluster_name = var.cluster_name
  attestor_id = module.attestor_creation.attestor_name
}
