import org.gradle.kotlin.dsl.configure
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:05
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Room {
  // https://developer.android.com/jetpack/androidx/releases/room?hl=en
  const val room_version = "2.5.2"
  
  const val `room-runtime` = "androidx.room:room-runtime:$room_version"
  const val `room-compiler` = "androidx.room:room-compiler:$room_version"
  
  // https://developer.android.google.cn/kotlin/ktx?hl=zh_cn#room
  const val `room-ktx` = "androidx.room:room-ktx:$room_version"
  const val `room-rxjava3` = "androidx.room:room-rxjava3:$room_version"
  const val `room-paging` = "androidx.room:room-paging:$room_version"
}

fun DependLibraryScope.dependRoom() {
  // ksp 按需引入
  apply(plugin = "com.google.devtools.ksp")
  extensions.configure<KspExtension> {
    arg("room.schemaLocation", "${project.projectDir}/schemas") // room 的架构导出目录
    arg("room.incremental", "true")
  }
  dependencies {
    "implementation"(Room.`room-runtime`)
    "implementation"(Room.`room-ktx`)
    "ksp"(Room.`room-compiler`)
  }
}

fun DependLibraryScope.dependRoomRxjava() {
  dependencies {
    "implementation"(Room.`room-rxjava3`)
  }
}

fun DependLibraryScope.dependRoomPaging() {
  dependencies {
    "implementation"(Room.`room-paging`)
  }
}