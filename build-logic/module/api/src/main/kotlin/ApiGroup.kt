import config.ApiConfigImpl
import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * 2023/9/6 00:11
 */
object ApiGroup {

  fun config(project: Project) {
    LibraryModule.config(ApiConfigImpl(project))
    project.parent ?: throw IllegalStateException("${project.name} 模块不存在父模块，" +
        "按照规定: api 模块的实现模块有且只能有父模块一个\n" +
        "如果不遵守将导致单模块出错 !!!")
  }
}