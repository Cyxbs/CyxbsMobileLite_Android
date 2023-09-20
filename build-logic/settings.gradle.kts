pluginManagement {
  includeBuild(".")
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // mavenCentral 快照仓库
    maven("https://jitpack.io")
    jcenter() // 部分依赖需要
    mavenLocal() // maven 默认的本地依赖位置：用户名/.m2/repository 中
  }
  // 开启 versionCatalogs 功能
  versionCatalogs {
    // 名称固定写成 libs
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}
rootProject.name = "build-logic"

// dependencies
include(":dependencies:library")

// module
include(":module:api")
include(":module:base")
include(":module:cyxbs:applications")
include(":module:cyxbs:components")
include(":module:cyxbs:functions")
include(":module:cyxbs:pages")
include(":module:plugins")

// plugin
include(":plugins:checker")