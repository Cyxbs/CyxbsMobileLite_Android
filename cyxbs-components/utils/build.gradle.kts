import utils.libsVersion

plugins {
  id("module-manager")
}

dependencies {
  val androidWheelVersion = libsVersion("androidWheel").requiredVersion
  api("io.github.985892345:utils-context:$androidWheelVersion")
  api("io.github.985892345:utils-adapter:$androidWheelVersion")
  api("io.github.985892345:utils-view:$androidWheelVersion")
  api("io.github.985892345:extensions-android:$androidWheelVersion")
  api("io.github.985892345:extensions-gson:$androidWheelVersion")
  api("io.github.985892345:extensions-rxjava:$androidWheelVersion")
  api("io.github.985892345:extensions-rxpermissions:$androidWheelVersion")
}