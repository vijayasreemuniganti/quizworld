
resource "google_binary_authorization_attestor" "attestor" {
   count  = length(var.attestor_name)
  name = var.attestor_name[count.index]
  attestation_authority_note {
    note_reference = google_container_analysis_note.note.name
    public_keys {
      id  = data.google_kms_crypto_key_version.version.id
      pkix_public_key {
        public_key_pem      = data.google_kms_crypto_key_version.version.public_key[0].pem
        signature_algorithm = data.google_kms_crypto_key_version.version.public_key[0].algorithm
      }
    }
  }
}


resource "google_container_analysis_note" "note" {
  name = var.attestor_note
  attestation_authority {
    hint {
      human_readable_name = "Attestor Note"
    }
  }
}


data "google_kms_crypto_key_version" "version" {
  crypto_key = var.crypto_key_id
}
