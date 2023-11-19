plugins {
  id("module-manager")
}

dependModule {
  dependBase()
  dependRouter()
  dependUtils()
  dependConfig()
}

dependLibrary {
}