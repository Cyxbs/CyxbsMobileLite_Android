plugins {
  id("module-manager")
}

dependModule {
  dependBase()
  dependRouter()
  dependUtils()
  dependView()
  dependInit()
}

dependLibrary {
}