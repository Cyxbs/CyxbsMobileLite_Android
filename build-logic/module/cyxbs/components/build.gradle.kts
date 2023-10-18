plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(project(":module:base"))
  implementation(project(":dependencies"))
  implementation(libs.ktProvider.gradlePlugin)
}