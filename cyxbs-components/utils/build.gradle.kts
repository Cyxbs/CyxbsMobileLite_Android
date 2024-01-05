plugins {
  id("module-manager")
}

dependencies {
  api(libs.androidWheel.extensions.android)
  api(libs.androidWheel.extensions.rxjava)
  api(libs.androidWheel.utils.adapter)
  api(libs.androidWheel.utils.context)
  api(libs.androidWheel.utils.impl)
  api(libs.androidWheel.utils.view)
  api(libs.androidWheel.jvm.exception)
  api(libs.androidWheel.jvm.flow)
  api(libs.androidWheel.jvm.generics)
  api(libs.androidWheel.jvm.impl)
  api(libs.androidWheel.jvm.rxjava)
}