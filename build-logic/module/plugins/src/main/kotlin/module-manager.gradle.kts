
if (name.startsWith("api-")) {
  ApiGroup.config(project)
} else {
  val parentFile: File = project.projectDir.parentFile
  val cyxbsGroupName = if (name.startsWith(parentFile.name)) {
    // 子模块
    parentFile.parentFile.name
  } else {
    // 父模块
    parentFile.name
  }
  when (cyxbsGroupName) {
    "cyxbs-applications" -> CyxbsApplicationsGroup.config(project)
    "cyxbs-components" -> CyxbsComponentsGroup.config(project)
    "cyxbs-functions" -> CyxbsFunctionsGroup.config(project)
    "cyxbs-pages" -> CyxbsPagesGroup.config(project)
    else -> throw Exception("出现未知类型模块: name = $name   dir = $projectDir\n请为该模块声明对应的依赖插件！")
  }
}
