plugins {
  id("module-manager")
}

dependencies {
  api(libs.androidWheel.utils.context)
}

dependLibrary {
  dependRxjava()
}