plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(project(":plugins:checker"))
  implementation(project(":dependencies"))
  implementation(libs.ktProvider.gradlePlugin)

  api(libs.android.gradlePlugin)
  api(libs.kotlin.gradlePlugin)
  api(libs.ksp.gradlePlugin)
}