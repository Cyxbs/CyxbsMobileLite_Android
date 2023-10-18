plugins {
  id("module-single")
}

dependModule {
  dependBase()
  dependConfig()
  dependRouter()
  dependUtils()
  dependView()
}

dependLibrary {
}

kotlin {
  compilerOptions {
    freeCompilerArgs
  }
}

ktProvider {

}

