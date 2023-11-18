plugins {
  id("module-manager")
}

dependModule {
  dependBase()
  dependRouter()
  dependView()
  dependApiSource()
  dependScriptPy()
}

dependLibrary {
}