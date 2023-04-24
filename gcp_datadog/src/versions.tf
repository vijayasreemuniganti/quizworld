terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 3.0"
    }

    datadog = {
      source = "DataDog/datadog"
    }

  }
}

provider "google" {
  credentials = var.gcp_credentials_path
  project     = var.gcp_project_id
  region      = var.gcp_region
  zone        = var.gcp_zone

}

# Configure the Datadog provider
provider "datadog" {
  api_key =  "4029909968b461c62e10a3e39629c011"
  #var.datadog_api_key
  app_key = "d259cac266949dec50b5f0bc333d1ced479917b0"
  #var.datadog_app_key
}