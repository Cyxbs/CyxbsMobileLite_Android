plugins {
  id("module-manager")
}

dependencies {
  implementation(libs.ktProvider.manager) // https://github.com/985892345/KtProvider
  implementation(libs.androidWheel.utils.context)
}