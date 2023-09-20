import org.gradle.configurationcache.extensions.capitalized


///////////  生成 depend* 方法  /////////////
subprojects.filter {
  when (it.name) {
    "components", "functions", "pages" -> true
    else -> false
  }
}.forEach { cyxbs ->
  val taskProvider = cyxbs.tasks.register("generateCyxbs${cyxbs.name.capitalized()}Depend") {
    val moduleNames = cyxbs.rootDir.parentFile.resolve("cyxbs-${cyxbs.name}")
      .listFiles { file ->
        file.resolve("build.gradle.kts").exists()
      }?.map { file ->
        // 如果存在 api 模块则替换为 api 模块
        file.resolve("api-${file.name}").let { if (it.exists()) it else file }
      } ?: emptyList()
    inputs.property("moduleNames", moduleNames.map { it.name })
    val outputDir = cyxbs.buildDir.resolve("generated")
      .resolve("sources")
      .resolve("depend")
    outputs.dir(outputDir)
    doLast {
      val text = moduleNames.joinToString("\n") { file ->
        """
        
        fun DependModuleScope.depend${file.name.capitalized()}() {
          dependencies {
            "implementation"(project(":cyxbs-${cyxbs.name}:${file.name}"))
          }
        }
      """.trimIndent()
      }
      outputDir.resolve("${cyxbs.name.capitalized()}Depend.kt")
        .writeText("// 使用 gradle 脚本生成，task 为 $name\n" +
            "import org.gradle.kotlin.dsl.dependencies\n" +
            text
        )
    }
  }

  /**
   * 等价于：
   * ```
   * sourceSet {
   *   main {
   *     kotlin {
   *       srcDir(...)
   *     }
   *   }
   * }
   * ```
   */
  cyxbs.afterEvaluate {
    cyxbs.configure<SourceSetContainer> {
      named("main") {
        (this as ExtensionAware).extensions.configure<SourceDirectorySet> {
          // 将上面的 task 的输出作为编译目录
          srcDir(taskProvider)
        }
      }
    }
  }
}
///////////  生成 depend* 方法  /////////////
