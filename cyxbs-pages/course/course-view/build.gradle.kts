plugins {
  id("module-manager")
}

dependModule {
  dependConfig(DependType.Api)
}

dependLibrary {
}

dependencies {
  api(libs.androidWheel.utils.view)
  // 20 级郭祥瑞封装的课表底层控件，如果有问题，欢迎来联系 👀
  api("io.github.985892345:NetLayout:1.1.2-SNAPSHOT")
}

configurations.all {
  resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}