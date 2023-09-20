plugins {
  id("module-manager")
}

dependLibrary {
  dependCoroutines()
  dependRxjava()
}

dependencies {
  api("io.github.985892345:base-ui:0.0.1-alpha04-SNAPSHOT")
  api("io.github.985892345:base-databinding:0.0.1-alpha04-SNAPSHOT")
}
