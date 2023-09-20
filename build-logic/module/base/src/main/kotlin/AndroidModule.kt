import config.AndroidConfig
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import utils.androidBase
import utils.kotlin
import utils.kotlinOptions
import utils.libsVersion

/**
 * .
 *
 * @author 985892345
 * 2023/9/5 16:11
 */
internal object AndroidModule {

  fun config(config: AndroidConfig) {
    // 项目检查工具
    AndroidProjectChecker.config(config.project)
    with(config.project) {
      apply(plugin = "kotlin-android")
      // 配置 android 闭包
      configAndroid(config)
      // 配置 kotlin 闭包
      kotlin {
        jvmToolchain(libsVersion("kotlinJvmTarget").requiredVersion.toInt())
      }
      // 默认依赖
      dependLibrary {
        _dependAndroidBase()
        _dependAndroidView()
        _dependAndroidKtx()
        _dependLifecycleKtx()
      }
    }
  }

  private fun Project.configAndroid(config: AndroidConfig) {
    androidBase {

      namespace = config.getNamespace()
      compileSdk = config.compileSdk

      defaultConfig {
        minSdk = config.minSdk
      }

      buildTypes {
        debug {
          isMinifyEnabled = false
          proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            rootDir.resolve("build-logic")
              .resolve("module")
              .resolve("proguard-rules.pro")
          )
        }
        release {
          isMinifyEnabled = true
          proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            rootDir.resolve("build-logic")
              .resolve("module")
              .resolve("proguard-rules.pro")
          )
        }
      }

      lint {
        abortOnError = false // 编译遇到错误不退出
      }

      // 命名规范设置，因为多模块相同资源名在打包时会合并，所以必须强制开启
      resourcePrefix = project.name.substringAfter("-")

      packaging {
        jniLibs.excludes += AndroidConfig.jniExclude
        resources.excludes += AndroidConfig.resourcesExclude
      }

      compileOptions {
        val javaVersion = libsVersion("javaTarget").requiredVersion
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
      }

      kotlinOptions {
        jvmTarget = libsVersion("kotlinJvmTarget").requiredVersion
      }
    }
  }
}