plugins {
  id("module-manager")
}

dependModule {
  dependBase()
  dependRouter()
  dependView()
  dependScriptWebview()
  dependApiSource()
}

dependLibrary {
  dependSerializationJson()
}
