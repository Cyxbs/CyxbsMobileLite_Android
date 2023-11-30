plugins {
  id("module-single")
}

dependModule {
  dependBase()
  dependConfig()
  dependRouter()
  dependUtils()
  dependView()
  dependApiNetwork()
}

dependLibrary {
  dependSerializationJson()
}