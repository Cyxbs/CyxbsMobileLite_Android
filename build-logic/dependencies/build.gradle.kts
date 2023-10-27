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
val include: List<String> = listOf(
)

val taskProvider = tasks.register("generateCyxbsDepend") {
  group = "cyxbs"
  val moduleFiles = listOf(
    rootDir.parentFile.resolve("cyxbs-components"),
    rootDir.parentFile.resolve("cyxbs-functions"),
    rootDir.parentFile.resolve("cyxbs-pages"),
  ).map { groupFile ->
    groupFile to groupFile.listFiles { file ->
      file.resolve("build.gradle.kts").exists()
    }!!.mapNotNull { file ->
      when (groupFile.name) {
        "cyxbs-components" -> file
        "cyxbs-functions" -> file.resolve("api-${file.name}").let { if (it.exists()) it else file }
        "cyxbs-pages" -> file.resolve("api-${file.name}").let { if (it.exists()) it else null }
        else -> null
      }
    }.filter {
      !include.contains(it.name)
    }
  }
  val outputDir = layout.buildDirectory.dir(
    "generated/source/depend/${SourceSet.MAIN_SOURCE_SET_NAME}"
  )
  // inputs 用于缓存 task
  inputs.property("moduleNames", moduleFiles.toString())
  // outputs 会作为 srcDir
  outputs.dir(outputDir)
  doLast {
    val outputDirFile = outputDir.get().asFile
    outputDirFile.deleteRecursively()
    outputDirFile.mkdirs()
    moduleFiles.forEach { pair ->
      val text = pair.second.joinToString("\n") { file ->
        val functionName = file.name
          .split("-")
          .joinToString("", "DependModuleScope.depend") {
            it.capitalized()
          }
        val projectPath = if (file.name.startsWith("api-")) {
          ":${pair.first.name}:${file.parentFile.name}:${file.name}"
        } else {
          ":${pair.first.name}:${file.name}"
        }
        """
        
        fun $functionName() {
          dependencies {
            "implementation"(project("$projectPath"))
          }
        }
      """.trimIndent()
      }
      val fileName = "${pair.first.name.split("-").joinToString("") { it.capitalized() }}Depend.kt"
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
///////////  生成 depend* 方法  /////////////
