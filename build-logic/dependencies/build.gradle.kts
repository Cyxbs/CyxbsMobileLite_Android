import org.gradle.configurationcache.extensions.capitalized

plugins {
  `kotlin-dsl`
}

dependencies {
  api(libs.android.gradlePlugin)
  api(libs.kotlin.gradlePlugin)
  api(libs.ksp.gradlePlugin)
}


///////////  生成 depend* 方法  /////////////

// 用于排除模块，写模块名即可
val excludeList: List<String> = listOf(
  "debug", // debug 模块单独依赖，不生成 depend 方法
)

val taskProvider = tasks.register("generateCyxbsDepend") {
  group = "cyxbs"
  val allModulePaths = mutableMapOf(
    "cyxbs-components" to mutableListOf<String>(),
    "cyxbs-functions" to mutableListOf(),
    "cyxbs-pages" to mutableListOf(),
  ).onEach {
    getAllModulePath(it.value, it.key, rootDir.parentFile.resolve(it.key))
  }
  val outputDir = layout.buildDirectory.dir(
    "generated/source/depend/${SourceSet.MAIN_SOURCE_SET_NAME}"
  )
  val dependModuleFile = projectDir.resolve("src")
    .resolve("main")
    .resolve("kotlin")
    .resolve("utils")
    .resolve("DependModule.kt")
  // inputs 用于缓存 task
  inputs.property("allModulePaths", allModulePaths)
  inputs.file(dependModuleFile)
  // outputs 会作为 srcDir
  outputs.dir(outputDir)
  doLast {
    val outputDirFile = outputDir.get().asFile
    outputDirFile.deleteRecursively()
    outputDirFile.mkdirs()
    // 排除 DependModule 中已经包含的函数
    val includeFunctionName = dependModuleFile.readLines()
      .filter { it.startsWith("fun DependModuleScope.depend") }
      .map { it.substringAfter("fun ").substringBefore("(") }
    allModulePaths.forEach { entry ->
      val text = entry.value
        .map { path ->
          // 转换为函数名字
          path to path.substringAfterLast(":")
            .split("-")
            .joinToString("", "DependModuleScope.depend") { it.capitalized() }
        }.filter { it.second !in includeFunctionName }
        .joinToString("\n") {
          """
            
            fun ${it.second}() {
              dependencies {
                "implementation"(project("${it.first}"))
              }
            }
          """.trimIndent()
        }
      val fileName = "${entry.key.split("-").joinToString("") { it.capitalized() }}Depend.kt"
      outputDirFile.resolve(fileName)
        .writeText(
          "// 使用 gradle 脚本生成，task 为 $name \n" +
              "// 详细可查看 build-logic/dependencies/build.gradle.kts \n" +
              "import org.gradle.kotlin.dsl.dependencies \n" +
              text
        )
    }
  }
}

sourceSets {
  main {
    // 将上面的 task 的输出作为编译目录
    kotlin.srcDir(taskProvider)
  }
}

// 递归得到所有模块的路径
fun getAllModulePath(result: MutableList<String>, topName: String, file: File) {
  if (!file.resolve("settings.gradle.kts").exists()) {
    if (file.resolve("build.gradle.kts").exists()) {
      var path = ":${file.name}"
      var parentFile = file.parentFile
      do {
        path = ":${parentFile.name}$path"
        parentFile = parentFile.parentFile
      } while (parentFile.name == topName)
      result.add(path)
    }
  }
  // 递归寻找所有子模块
  file.listFiles()?.filter {
    it.name != "src" // 去掉 src 文件夹
        && !it.resolve("settings.gradle.kts").exists() // 去掉独立的项目模块，比如 build-logic
        && !excludeList.contains(it.name) // 去掉被忽略的模块
  }?.forEach {
    getAllModulePath(result, topName, it)
  }
}
///////////  生成 depend* 方法  /////////////
