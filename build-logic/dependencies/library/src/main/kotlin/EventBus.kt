import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:48
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object EventBus {
  // https://github.com/greenrobot/EventBus
  const val eventBus = "org.greenrobot:eventbus:3.3.1"

  // eventbus 默认采取反射获取，但官方也提供了注解处理器
  // 目前因为 EventBus 用的少，所以就暂时不用注解处理器吧，因为降低编译速度
  // https://greenrobot.org/eventbus/documentation/subscriber-index/
}

fun DependLibraryScope.dependEventBus() {
  dependencies {
    "implementation"(EventBus.eventBus)
  }
}