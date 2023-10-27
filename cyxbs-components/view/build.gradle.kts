import utils.libsVersion

plugins {
  id("module-manager")
}

dependModule {
  dependBase()
  dependUtils()
  dependConfig()
}

dependencies {
  val androidWheelVersion = libsVersion("androidWheel").requiredVersion
  api("io.github.985892345:base-ui:$androidWheelVersion")
}