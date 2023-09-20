val parentFileName: String = project.projectDir.parentFile.name

when (parentFileName) {
  "cyxbs-applications" -> CyxbsApplicationsGroup.config(project)
  "cyxbs-components" -> CyxbsComponentsGroup.config(project)
  "cyxbs-functions" -> CyxbsFunctionsGroup.config(project)
  "cyxbs-pages" -> CyxbsPagesGroup.config(project)
  else -> {
    when {
      name.startsWith("api-") -> ApiGroup.config(project)
      else -> throw Exception("出现未知类型模块: name = $name   dir = $projectDir\n请为该模块声明对应的依赖插件！")
    }
  }
}
