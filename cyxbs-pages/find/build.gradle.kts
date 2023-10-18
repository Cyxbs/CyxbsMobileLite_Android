plugins {
  id("module-single")
}

dependModule {
  dependConfig()
  dependUtils()
  dependView()
  dependBase()
}

dependLibrary {
  dependCoroutines()
}