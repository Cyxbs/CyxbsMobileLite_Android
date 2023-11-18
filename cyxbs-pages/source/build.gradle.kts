plugins {
  id("module-single")
}

moduleConfig {
  isNeedDependChild = false // 不自动依赖子模块
}

dependModule {
  // 单独对子模块依赖进行设置，其他模块依赖请设置在下面的 dependModule 闭包
  dependApiSource()
  dependSourceWebview()
  dependSourcePy()
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