import utils.libsVersion

plugins {
  id("module-manager")
}

dependModule {
  dependBase()
}

dependencies {
  // https://github.com/985892345/KtProvider
  val ktProviderVersion = libsVersion("ktProvider").requiredVersion
  implementation("io.github.985892345:provider-manager-jvm:$ktProviderVersion")
}