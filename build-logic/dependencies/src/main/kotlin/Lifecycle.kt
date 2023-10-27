import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 14:25
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Lifecycle {
  // 官方 lifecycle 扩展
  // https://developer.android.google.cn/jetpack/androidx/releases/lifecycle
  // https://developer.android.google.cn/kotlin/ktx/extensions-list?hl=zh_cn#androidxlifecycle
  const val lifecycle_version = "2.6.2"
  const val `viewmodel-ktx` = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
  const val `livedata-ktx` = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
  // Lifecycles only (without ViewModel or LiveData)
  const val `runtime-ktx` = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"

  // Annotation processor (这个官方已经废弃，不建议再使用)
//  const val `lifecycle-compiler` = "androidx.lifecycle:lifecycle-compiler:$lifecycle_version"
  
  // 下面这几个是可选的，默认不主动依赖

  // optional - helpers for implementing LifecycleOwner in a Service
  const val `lifecycle-service` = "androidx.lifecycle:lifecycle-service:$lifecycle_version"
  
  // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
  const val `lifecycle-process` = "androidx.lifecycle:lifecycle-process:$lifecycle_version"
  
  // optional - ReactiveStreams support for LiveData
  // LiveData 与 Rxjava、Flow 的转换
  const val `lifecycle-reactivestreams-ktx` = "androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycle_version"
}

/*
* 所有 module 模块都已经默认依赖
* */
fun DependLibraryScope._dependLifecycleKtx() {
  dependencies {
    "implementation"(Lifecycle.`viewmodel-ktx`)
    "implementation"(Lifecycle.`livedata-ktx`)
    "implementation"(Lifecycle.`runtime-ktx`)
  }
}

// 带有 Lifecycle 的 Service
fun DependLibraryScope.dependLifecycleService() {
  dependencies {
    "implementation"(Lifecycle.`lifecycle-service`)
  }
}

// LiveData 转换成 Rxjava 或者 Flow
fun DependLibraryScope.dependLiveData2RxFlow() {
  dependencies {
    "implementation"(Lifecycle.`lifecycle-reactivestreams-ktx`)
  }
}

// 应用的生命周期 ProcessLifecycleOwner，但建议使用项目中的 processLifecycle
fun DependLibraryScope.dependLifecycleProcess() {
  dependencies {
    "implementation"(Lifecycle.`lifecycle-process`)
  }
}

