plugins {
  id("module-manager")
}

dependModule {
  dependCourseView(DependType.Api)
}

dependLibrary {
}

dependencies {
  api(libs.androidWheel.utils.view)
  api(libs.androidWheel.base.ui)
  api(libs.androidWheel.extensions.android)
}

configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}