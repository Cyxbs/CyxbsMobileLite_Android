plugins {
  id("module-manager")
}

dependModule {
  dependRouter()
}

dependLibrary {
  dependOkHttp()
  dependRetrofit()
  dependSerializationJson()
}

dependencies {
  implementation(Network.`converter-kotlinx-serialization`)
  implementation(Network.`logging-interceptor`)
  implementation(Network.`adapter-rxjava3`)
}