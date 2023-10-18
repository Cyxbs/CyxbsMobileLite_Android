
// 是否禁止单模块
val isBanSingleModule: Boolean = when {
  name.startsWith("api-") -> false
  else -> {
    // gradle 执行命令判断
    gradle.startParameter.taskNames.any {
      it.contains("Release")
          || it.contains("Debug") && !it.contains(name) // 不是自身模块使用 Debug
    }
  }
}

if (isBanSingleModule) {
  println("$name 的单模块调试被取消！")
  apply(plugin = "module-manager")
} else {
  when (project.projectDir.parentFile.name) {
    "cyxbs-components" -> CyxbsComponentsGroup.config(project, true)
    "cyxbs-functions" -> CyxbsFunctionsGroup.config(project, true)
    "cyxbs-pages" -> CyxbsPagesGroup.config(project, true)
    else -> throw Exception("出现未知类型模块: name = $name   dir = $projectDir\n请为该模块声明对应的依赖插件！")
  }
}