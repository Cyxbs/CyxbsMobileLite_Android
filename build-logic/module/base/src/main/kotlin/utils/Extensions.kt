package utils

import com.android.build.api.dsl.AndroidResources
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.ProductFlavor
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

/**
 * .
 *
 * @author 985892345
 * 2023/9/5 16:19
 */

fun Project.androidLib(configure: LibraryExtension.() -> Unit) {
  extensions.configure(configure)
}

fun Project.androidApp(configure: BaseAppModuleExtension.() -> Unit) {
  extensions.configure(configure)
}

fun Project.androidBase(
  configure: CommonExtension<
      BuildFeatures, BuildType, DefaultConfig, ProductFlavor, AndroidResources>.() -> Unit
) {
  extensions.configure("android", configure)
}

fun Project.kotlinBlock(configure: KotlinAndroidProjectExtension.() -> Unit) {
  extensions.configure(configure)
}

fun CommonExtension<*, *, *, *, *>.kotlinOptionsBlock(configure: KotlinJvmOptions.() -> Unit) {
  (this as ExtensionAware).extensions.configure(configure)
}





/**
 * LibraryExtension 本身是没有实现 ExtensionAware，但是其最终的子类实现了 ExtensionAware
 * 这里这样写主要是为了作用域的问题，防止拿到 Project.extensions
 * ```
 * fun Project.test() {
 *   android {
 *     extensions
 *     // 这个本意上是使用 android 作用域中的 extensions，
 *     // 但是因为 LibraryExtension 并没有实现 ExtensionAware，导致没有 extensions 这个方法
 *     // 从而实际上是使用的 Project.extensions (所以给 LibraryExtension 添加扩展变量来避免这个问题)
 *   }
 * }
 *
 * fun android(configure: LibraryExtension.() -> Unit) {
 *   // ...
 * }
 * ```
 */
val LibraryExtension.extensions: ExtensionContainer
  get() = (this as ExtensionAware).extensions

// 原因同 LibraryExtension
val BaseAppModuleExtension.extensions: ExtensionContainer
  get() = (this as ExtensionAware).extensions