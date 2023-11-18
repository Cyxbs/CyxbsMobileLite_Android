plugins {
  id("module-manager")
}

dependModule {
  dependBase()
  dependRouter()
  dependUtils()
}

dependLibrary {
}

dependencies {
//  implementation("org.mozilla:rhino:1.7.15-SNAPSHOT")
}