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
  dependApiSource()
}

dependLibrary {
  dependRoom()
  dependSerializationJson()
}