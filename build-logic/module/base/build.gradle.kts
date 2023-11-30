plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(project(":plugins:checker"))
  implementation(libs.ktProvider.gradlePlugin)

  api(project(":dependencies"))

  api(libs.android.gradlePlugin)
  api(libs.kotlin.gradlePlugin)
  api(libs.ksp.gradlePlugin)
}