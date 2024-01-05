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
  api(libs.androidWheel.base.ui)
  api(libs.androidWheel.base.databinding)
}

//configurations.all {
//  resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
//}