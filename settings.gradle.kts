pluginManagement {
  includeBuild("build-logic")
  repositories {
    mavenCentral()
    mavenLocal()
    google()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // mavenCentral 快照仓库
    gradlePluginPortal()
    maven("https://chaquo.com/maven")
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    mavenLocal() // maven 默认的本地依赖位置：用户名/.m2/repository 中
    google()
//    maven("https://jitpack.io")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // mavenCentral 快照仓库
  }
}

rootProject.name = "CyxbsMobileLite_Android"



///////////  自动 include 模块  ///////////

// 需要删除模块时写这里面，将不再进行 include，直接写模块名即可
val excludeList: List<String> = listOf(
  "script1"
)

fun includeModule(topName: String, file: File) {
  if (!file.resolve("settings.gradle.kts").exists()) {
    if (file.resolve("build.gradle.kts").exists()) {
      var path = ":${file.name}"
      var parentFile = file.parentFile
      do {
        path = ":${parentFile.name}$path"
        parentFile = parentFile.parentFile
      } while (parentFile.name == topName)
      include(path)
    }
  }
  // 递归寻找所有子模块
  file.listFiles()?.filter {
    it.name != "src" // 去掉 src 文件夹
        && !it.resolve("settings.gradle.kts").exists() // 去掉独立的项目模块，比如 build-logic
        && !excludeList.contains(it.name) // 去掉被忽略的模块
  }?.forEach {
    includeModule(topName, it)
  }
}

includeModule("cyxbs-applications", rootDir.resolve("cyxbs-applications"))
includeModule("cyxbs-components", rootDir.resolve("cyxbs-components"))
includeModule("cyxbs-functions", rootDir.resolve("cyxbs-functions"))
includeModule("cyxbs-pages", rootDir.resolve("cyxbs-pages"))
/**
 * 如果你使用 AS 自带的模块模版，他会自动添加 include()，请删除掉，因为上面会自动读取
 * 请注意:
 * - 对于普通的模块请使用配套的 idea 插件: CyxbsModuleBuilder
 * - 如果是比较特殊的模块，请单独 include()
 */





// 如果 build 窗口乱码，去 顶部栏 - Help - Edit Custom VM Options 里面添加 -Dfile.encoding=UTF-8，然后重启 AS
// 制作网址：http://patorjk.com/software/taag/
println("""
    ______  __      __  __    __  _______    ______  
   /      \|  \    /  \|  \  |  \|       \  /      \ 
  |  ▓▓▓▓▓▓\\▓▓\  /  ▓▓| ▓▓  | ▓▓| ▓▓▓▓▓▓▓\|  ▓▓▓▓▓▓\
  | ▓▓   \▓▓ \▓▓\/  ▓▓  \▓▓\/  ▓▓| ▓▓__/ ▓▓| ▓▓___\▓▓
  | ▓▓        \▓▓  ▓▓    >▓▓  ▓▓ | ▓▓    ▓▓ \▓▓    \ 
  | ▓▓   __    \▓▓▓▓    /  ▓▓▓▓\ | ▓▓▓▓▓▓▓\ _\▓▓▓▓▓▓\
  | ▓▓__/  \   | ▓▓    |  ▓▓ \▓▓\| ▓▓__/ ▓▓|  \__| ▓▓
   \▓▓    ▓▓   | ▓▓    | ▓▓  | ▓▓| ▓▓    ▓▓ \▓▓    ▓▓
    \▓▓▓▓▓▓     \▓▓     \▓▓   \▓▓ \▓▓▓▓▓▓▓   \▓▓▓▓▓▓ 
                                                     
""".trimIndent())
