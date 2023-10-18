import com.g985892345.provider.plugin.gradle.extensions.KtProviderExtensions
import com.g985892345.provider.plugin.gradle.generator.KtProviderInitializerGenerator
import config.SingleModuleConfig
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import utils.androidApp
import utils.kotlinBlock

/**
 * .
 *
 * @author 985892345
 * 2023/9/15 21:19
 */
object SingleModule {

  fun config(config: SingleModuleConfig) {
    ApplicationModule.config(config)
    LibraryModule.dependChildModule(config)
    with(config.project) {
      androidApp {
        sourceSets {
          getByName("main") {
            // 将 single 加入编译环境，单模块需要的代码放这里面
            java.srcDir("src/main/single")
          }
        }
      }
      dependModule {
        dependSinglemodule()
      }
    }
    setKtProviderEntryClass(config.project)
  }

  /**
   * 当 :cyxbs-component:singlemodule 模块配置时回调
   */
  fun onConfigSingleModuleProject(singleModule: Project) {
    srcDirKtProviderEntryClass(singleModule)
  }

  /**
   * 将 [setKtProviderEntryClass] 中生成的文件加入编译环境
   *
   * 为什么不写在 [setKtProviderEntryClass] 内 ?
   * 因为 [setKtProviderEntryClass] 是在 application 模块配置阶段触发，
   * 即使能拿到 singleModuleProject，也会因为 gradle Project 的加载顺序而无法设置东西
   */
  private fun srcDirKtProviderEntryClass(singleModule: Project) {
    val outputDir = singleModule.layout.buildDirectory.dir(
      "generated/source/ktProviderEntry/${SourceSet.MAIN_SOURCE_SET_NAME}"
    )
    singleModule.kotlinBlock {
      sourceSets.getByName("main")
        .kotlin
        .srcDir(outputDir)
    }
  }

  /**
   * 用于在 singleModuleProject 的 build 路径下生成 application 模块的路由文件类名
   *
   * 为什么要配置这个 task ?
   * 1. 该 task 用于生成 KtProviderInitializer 的实现类 (在 KtProvider 框架中)
   * 2. 因为 singlemodule 不是 application 模块，但他却作为了启动模块去拉起使用了 application 的模块，
   *    所以需要手动去加载 application 模块的路由文件，
   *    因为不能反向依赖，所以通过在编译期找到使用单模块的模块生成的路由文件类名，然后反射去加载
   */
  private fun setKtProviderEntryClass(project: Project) {
    val singleModuleProject = project.project(":cyxbs-components:singlemodule")
    val outputDir = singleModuleProject.layout.buildDirectory.dir(
      "generated/source/ktProviderEntry/${SourceSet.MAIN_SOURCE_SET_NAME}"
    )
    // 拿到 KtProvider 生成 KtProviderInitializer 实现类的 task
    project.tasks.named(KtProviderInitializerGenerator.getTaskName(project)) {
      doLast {
        val ktProviderException = project.extensions
          .getByType(KtProviderExtensions::class.java)
        outputDir.get().asFile.apply {
          deleteRecursively()
          mkdirs()
        }.resolve("GetKtProviderEntryClassName.kt")
          .writeText(
            "// 自动生成，任务为 $name \n" +
                "internal val ktProviderEntryClassName = " +
                "\"${ktProviderException.initializerClassPackage}." +
                "${ktProviderException.initializerClassName}\""
          )
      }
    }
  }
}