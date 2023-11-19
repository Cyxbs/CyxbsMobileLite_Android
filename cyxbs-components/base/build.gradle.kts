import utils.libsVersion

plugins {
  id("module-manager")
}

dependModule {
  dependInit()
  dependRouter()
  dependUtils()
}

dependLibrary {
  dependCoroutines()
  dependRxjava()
}

dependencies {
  val androidWheelVersion = libsVersion("androidWheel").requiredVersion
  api("io.github.985892345:base-ui:$androidWheelVersion")
  api("io.github.985892345:base-databinding:$androidWheelVersion")
}
