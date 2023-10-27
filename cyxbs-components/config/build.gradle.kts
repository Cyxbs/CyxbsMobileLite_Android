import utils.libsVersion

plugins {
  id("module-manager")
}

dependencies {
  val androidWheelVersion = libsVersion("androidWheel").requiredVersion
  api("io.github.985892345:utils-context:$androidWheelVersion")
}

dependLibrary {
  dependRxjava()
}