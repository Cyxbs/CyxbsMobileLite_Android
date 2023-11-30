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

dependModule {
  // 目前数据源使用 source 模块，存在后续替换数据源的可能性
  runtimeOnlySource()
}