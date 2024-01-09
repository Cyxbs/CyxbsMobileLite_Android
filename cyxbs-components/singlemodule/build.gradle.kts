plugins {
  id("module-manager")
}

dependModule {
  dependBase()
  dependConfig()
  dependRouter()
  dependView()
}