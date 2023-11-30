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
  dependApiAccount()
}

dependLibrary {
  dependRoom()
  dependRoomRxjava()
  dependSerializationJson()
}