plugins {
  id("module-manager")
}

dependModule {
  dependBase()
  dependUtils()
  dependRouter()
}

dependLibrary {
  dependOkHttp()
  dependRetrofit()
  dependSerializationJson()
  dependCoroutines2Rx()
}
