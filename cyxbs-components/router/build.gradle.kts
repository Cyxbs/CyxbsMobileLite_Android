import utils.libsVersion

plugins {
  id("module-manager")
}

dependencies {
  // https://github.com/985892345/KtProvider
  val ktProviderVersion = libsVersion("ktProvider").requiredVersion
  implementation("io.github.985892345:provider-manager-jvm:$ktProviderVersion")
  val androidWheelVersion = libsVersion("androidWheel").requiredVersion
  implementation("io.github.985892345:utils-context:$androidWheelVersion")
}