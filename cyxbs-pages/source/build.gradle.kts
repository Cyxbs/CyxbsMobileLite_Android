plugins {
  id("module-single")
}

dependModule {
  dependInit()
  dependBase()
  dependConfig()
  dependRouter()
  dependUtils()
  dependView()
  dependApiNetwork()
}

dependLibrary {
  dependRoom()
  dependCoroutines2Rx()
  dependSerializationJson()
}