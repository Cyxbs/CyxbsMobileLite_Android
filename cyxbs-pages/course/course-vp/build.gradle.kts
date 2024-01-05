plugins {
  id("module-manager")
}

dependModule {
  dependCoursePage(DependType.Api)
}

dependLibrary {
}

dependencies {
  // https://mvnrepository.com/artifact/androidx.customview/customview-poolingcontainer
  implementation("androidx.customview:customview-poolingcontainer:1.0.0")
}