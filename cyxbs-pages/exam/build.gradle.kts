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
  dependApiAccount()
}

dependLibrary {
  dependRoom()
  dependRoomRxjava()
  dependSerializationJson()
}