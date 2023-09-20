plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(project(":plugins:checker"))
  api(project(":dependencies:library"))

  api(libs.android.gradlePlugin)
  api(libs.kotlin.gradlePlugin)
  api(libs.ksp.gradlePlugin)
}