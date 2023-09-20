pluginManagement {
  includeBuild("build-logic")
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // mavenCentral 快照仓库
    maven("https://jitpack.io")
    jcenter() // 部分依赖需要
    mavenLocal() // maven 默认的本地依赖位置：用户名/.m2/repository 中
  }
}

rootProject.name = "CyxbsMobileLite_Android"

// cyxbs-applications
include(":cyxbs-applications:cyxbs-lite")

// cyxbs-components
include(":cyxbs-components:view")
include(":cyxbs-components:utils")
include(":cyxbs-components:singlemodule")
include(":cyxbs-components:config")
include(":cyxbs-components:base")

// cyxbs-functions
include(":cyxbs-functions:script")
include(":cyxbs-functions:debug")

// cyxbs-pages
include(":cyxbs-pages:exam")
