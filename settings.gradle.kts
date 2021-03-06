rootProject.name = "connector"

enableFeaturePreview("GRADLE_METADATA")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    jcenter()
    mavenCentral()
  }
}

include(":connector-codegen")
include(":connector-processor")
include(":connector-runtime-core")
include(":connector-runtime-http")
include(":e2e-tests")
include(":test-util")
