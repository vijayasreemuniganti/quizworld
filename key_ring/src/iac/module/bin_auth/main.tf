resource "google_binary_authorization_policy" "policy" {
  default_admission_rule {
    evaluation_mode         = "REQUIRE_ATTESTATION"
    enforcement_mode        = "ENFORCED_BLOCK_AND_AUDIT_LOG"
    require_attestations_by = var.attestor_id
  }

  global_policy_evaluation_mode = "ENABLE"
}

